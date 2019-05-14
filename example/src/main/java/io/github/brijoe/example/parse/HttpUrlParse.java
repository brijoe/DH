/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.brijoe.example.parse;

import java.net.IDN;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okio.Buffer;


final class HttpUrlParse {
    private static final char[] HEX_DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    private static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    private static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
    private static final String QUERY_ENCODE_SET = " \"'<>#";
    private static final String FRAGMENT_ENCODE_SET = "";

    /**
     * Either "http" or "https".
     */
    public String scheme;

    /**
     * Decoded username.
     */
    public String username;

    /**
     * Decoded password.
     */
    public String password;

    /**
     * Canonical hostname.
     */
    public String host;

    /**
     * Either 80, 443 or a user-specified port. In range [1..65535].
     */
    public int port;

    /**
     * A list of canonical path segments. This list always contains at least one element, which may be
     * the empty string. Each segment is formatted with a leading '/', so if path segments were ["a",
     * "b", ""], then the encoded path would be "/a/b/".
     */
    public List<String> pathSegments;

    /**
     * Alternating, decoded query names and values, or null for no query. Names may be empty or
     * non-empty, but never null. Values are null if the name has no corresponding '=' separator, or
     * empty, or non-empty.
     */
    public List<String> queryNamesAndValues;

    /**
     * Decoded fragment.
     */
    public String fragment;

    /**
     * Canonical URL.
     */
    public String url;

    private HttpUrlParse(Builder builder) {
        this.scheme = builder.scheme;
        this.username = percentDecode(builder.encodedUsername, false);
        this.password = percentDecode(builder.encodedPassword, false);
        this.host = builder.host;
        this.port = builder.effectivePort();
        this.pathSegments = percentDecode(builder.encodedPathSegments, false);
        this.queryNamesAndValues = builder.encodedQueryNamesAndValues != null
                ? percentDecode(builder.encodedQueryNamesAndValues, true)
                : null;
        this.fragment = builder.encodedFragment != null
                ? percentDecode(builder.encodedFragment, false)
                : null;
        this.url = builder.toString();
    }

    /**
     * Returns the username, or an empty string if none is set.
     */
    private String encodedUsername() {
        if (username.isEmpty()) return "";
        int usernameStart = scheme.length() + 3; // "://".length() == 3.
        int usernameEnd = delimiterOffset(url, usernameStart, url.length(), ":@");
        return url.substring(usernameStart, usernameEnd);
    }


    /**
     * Returns the password, or an empty string if none is set.
     */
    private String encodedPassword() {
        if (password.isEmpty()) return "";
        int passwordStart = url.indexOf(':', scheme.length() + 3) + 1;
        int passwordEnd = url.indexOf('@');
        return url.substring(passwordStart, passwordEnd);
    }


    /**
     * Returns 80 if {@code scheme.equals("http")}, 443 if {@code scheme.equals("https")} and -1
     * otherwise.
     */
    private static int defaultPort(String scheme) {
        if (scheme.equals("http")) {
            return 80;
        } else if (scheme.equals("https")) {
            return 443;
        } else {
            return -1;
        }
    }


    private List<String> encodedPathSegments() {
        int pathStart = url.indexOf('/', scheme.length() + 3);
        int pathEnd = delimiterOffset(url, pathStart, url.length(), "?#");
        List<String> result = new ArrayList<>();
        for (int i = pathStart; i < pathEnd; ) {
            i++; // Skip the '/'.
            int segmentEnd = delimiterOffset(url, i, pathEnd, '/');
            result.add(url.substring(i, segmentEnd));
            i = segmentEnd;
        }
        return result;
    }


    /**
     * Returns the query of this URL, encoded for use in HTTP resource resolution. The returned string
     * may be null (for URLs with no query), empty (for URLs with an empty query) or non-empty (all
     * other URLs).
     */
    private String encodedQuery() {
        if (queryNamesAndValues == null) return null; // No query.
        int queryStart = url.indexOf('?') + 1;
        int queryEnd = delimiterOffset(url, queryStart + 1, url.length(), '#');
        return url.substring(queryStart, queryEnd);
    }


    /**
     * Cuts {@code encodedQuery} up into alternating parameter names and values. This divides a query
     * string like {@code subject=math&easy&problem=5-2=3} into the list {@code ["subject", "math",
     * "easy", null, "problem", "5-2=3"]}. Note that values may be null and may contain '='
     * characters.
     */
    static List<String> queryStringToNamesAndValues(String encodedQuery) {
        List<String> result = new ArrayList<>();
        for (int pos = 0; pos <= encodedQuery.length(); ) {
            int ampersandOffset = encodedQuery.indexOf('&', pos);
            if (ampersandOffset == -1) ampersandOffset = encodedQuery.length();

            int equalsOffset = encodedQuery.indexOf('=', pos);
            if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
                result.add(encodedQuery.substring(pos, ampersandOffset));
                result.add(null); // No value for this name.
            } else {
                result.add(encodedQuery.substring(pos, equalsOffset));
                result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
            }
            pos = ampersandOffset + 1;
        }
        return result;
    }

    /**
     * Returns a new {@code HttpUrl} representing {@code url} if it is a well-formed HTTP or HTTPS
     * URL, or null if it isn't.
     */
    public static HttpUrlParse parse(String url) {
        Builder builder = new Builder();
        Builder.ParseResult result = builder.parse(null, url);
        return result == Builder.ParseResult.SUCCESS ? builder.build() : null;
    }


    private static final class Builder {
        String scheme;
        String encodedUsername = "";
        String encodedPassword = "";
        String host;
        int port = -1;
        final List<String> encodedPathSegments = new ArrayList<>();
        List<String> encodedQueryNamesAndValues;
        String encodedFragment;

        public Builder() {
            encodedPathSegments.add(""); // The default path is '/' which needs a trailing space.
        }


        int effectivePort() {
            return port != -1 ? port : defaultPort(scheme);
        }


        public Builder encodedQuery(String encodedQuery) {
            this.encodedQueryNamesAndValues = encodedQuery != null
                    ? queryStringToNamesAndValues(
                    canonicalize(encodedQuery, QUERY_ENCODE_SET, true, false, true, true))
                    : null;
            return this;
        }

        /**
         * Re-encodes the components of this URL so that it satisfies (obsolete) RFC 2396, which is
         * particularly strict for certain components.
         */


        public HttpUrlParse build() {
            if (scheme == null) throw new IllegalStateException("scheme == null");
            if (host == null) throw new IllegalStateException("host == null");
            return new HttpUrlParse(this);
        }


        enum ParseResult {
            SUCCESS,
            MISSING_SCHEME,
            UNSUPPORTED_SCHEME,
            INVALID_PORT,
            INVALID_HOST,
        }

        ParseResult parse(HttpUrlParse base, String input) {
            int pos = skipLeadingAsciiWhitespace(input, 0, input.length());
            int limit = skipTrailingAsciiWhitespace(input, pos, input.length());

            // Scheme.
            int schemeDelimiterOffset = schemeDelimiterOffset(input, pos, limit);
            if (schemeDelimiterOffset != -1) {
                if (input.regionMatches(true, pos, "https:", 0, 6)) {
                    this.scheme = "https";
                    pos += "https:".length();
                } else if (input.regionMatches(true, pos, "http:", 0, 5)) {
                    this.scheme = "http";
                    pos += "http:".length();
                } else {
                    return ParseResult.UNSUPPORTED_SCHEME; // Not an HTTP scheme.
                }
            } else if (base != null) {
                this.scheme = base.scheme;
            } else {
                return ParseResult.MISSING_SCHEME; // No scheme.
            }

            // Authority.
            boolean hasUsername = false;
            boolean hasPassword = false;
            int slashCount = slashCount(input, pos, limit);
            if (slashCount >= 2 || base == null || !base.scheme.equals(this.scheme)) {
                // Read an authority if either:
                //  * The input starts with 2 or more slashes. These follow the scheme if it exists.
                //  * The input scheme exists and is different from the base URL's scheme.
                //
                // The structure of an authority is:
                //   username:password@host:port
                //
                // Username, password and port are optional.
                //   [username[:password]@]host[:port]
                pos += slashCount;
                authority:
                while (true) {
                    int componentDelimiterOffset = delimiterOffset(input, pos, limit, "@/\\?#");
                    int c = componentDelimiterOffset != limit
                            ? input.charAt(componentDelimiterOffset)
                            : -1;
                    switch (c) {
                        case '@':
                            // User info precedes.
                            if (!hasPassword) {
                                int passwordColonOffset = delimiterOffset(
                                        input, pos, componentDelimiterOffset, ':');
                                String canonicalUsername = canonicalize(
                                        input, pos, passwordColonOffset, USERNAME_ENCODE_SET, true, false, false, true);
                                this.encodedUsername = hasUsername
                                        ? this.encodedUsername + "%40" + canonicalUsername
                                        : canonicalUsername;
                                if (passwordColonOffset != componentDelimiterOffset) {
                                    hasPassword = true;
                                    this.encodedPassword = canonicalize(input, passwordColonOffset + 1,
                                            componentDelimiterOffset, PASSWORD_ENCODE_SET, true, false, false, true);
                                }
                                hasUsername = true;
                            } else {
                                this.encodedPassword = this.encodedPassword + "%40" + canonicalize(input, pos,
                                        componentDelimiterOffset, PASSWORD_ENCODE_SET, true, false, false, true);
                            }
                            pos = componentDelimiterOffset + 1;
                            break;

                        case -1:
                        case '/':
                        case '\\':
                        case '?':
                        case '#':
                            // Host info precedes.
                            int portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                            if (portColonOffset + 1 < componentDelimiterOffset) {
                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                if (this.port == -1)
                                    return ParseResult.INVALID_PORT; // Invalid port.
                            } else {
                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                this.port = defaultPort(this.scheme);
                            }
                            if (this.host == null) return ParseResult.INVALID_HOST; // Invalid host.
                            pos = componentDelimiterOffset;
                            break authority;
                    }
                }
            } else {
                // This is a relative link. Copy over all authority components. Also maybe the path & query.
                this.encodedUsername = base.encodedUsername();
                this.encodedPassword = base.encodedPassword();
                this.host = base.host;
                this.port = base.port;
                this.encodedPathSegments.clear();
                this.encodedPathSegments.addAll(base.encodedPathSegments());
                if (pos == limit || input.charAt(pos) == '#') {
                    encodedQuery(base.encodedQuery());
                }
            }

            // Resolve the relative path.
            int pathDelimiterOffset = delimiterOffset(input, pos, limit, "?#");
            resolvePath(input, pos, pathDelimiterOffset);
            pos = pathDelimiterOffset;

            // Query.
            if (pos < limit && input.charAt(pos) == '?') {
                int queryDelimiterOffset = delimiterOffset(input, pos, limit, '#');
                this.encodedQueryNamesAndValues = queryStringToNamesAndValues(canonicalize(
                        input, pos + 1, queryDelimiterOffset, QUERY_ENCODE_SET, true, false, true, true));
                pos = queryDelimiterOffset;
            }

            // Fragment.
            if (pos < limit && input.charAt(pos) == '#') {
                this.encodedFragment = canonicalize(
                        input, pos + 1, limit, FRAGMENT_ENCODE_SET, true, false, false, false);
            }

            return ParseResult.SUCCESS;
        }

        private void resolvePath(String input, int pos, int limit) {
            // Read a delimiter.
            if (pos == limit) {
                // Empty path: keep the base path as-is.
                return;
            }
            char c = input.charAt(pos);
            if (c == '/' || c == '\\') {
                // Absolute path: reset to the default "/".
                encodedPathSegments.clear();
                encodedPathSegments.add("");
                pos++;
            } else {
                // Relative path: clear everything after the last '/'.
                encodedPathSegments.set(encodedPathSegments.size() - 1, "");
            }

            // Read path segments.
            for (int i = pos; i < limit; ) {
                int pathSegmentDelimiterOffset = delimiterOffset(input, i, limit, "/\\");
                boolean segmentHasTrailingSlash = pathSegmentDelimiterOffset < limit;
                push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
                i = pathSegmentDelimiterOffset;
                if (segmentHasTrailingSlash) i++;
            }
        }

        /**
         * Adds a path segment. If the input is ".." or equivalent, this pops a path segment.
         */
        private void push(String input, int pos, int limit, boolean addTrailingSlash,
                          boolean alreadyEncoded) {
            String segment = canonicalize(
                    input, pos, limit, PATH_SEGMENT_ENCODE_SET, alreadyEncoded, false, false, true);
            if (isDot(segment)) {
                return; // Skip '.' path segments.
            }
            if (isDotDot(segment)) {
                pop();
                return;
            }
            if (encodedPathSegments.get(encodedPathSegments.size() - 1).isEmpty()) {
                encodedPathSegments.set(encodedPathSegments.size() - 1, segment);
            } else {
                encodedPathSegments.add(segment);
            }
            if (addTrailingSlash) {
                encodedPathSegments.add("");
            }
        }

        private boolean isDot(String input) {
            return input.equals(".") || input.equalsIgnoreCase("%2e");
        }

        private boolean isDotDot(String input) {
            return input.equals("..")
                    || input.equalsIgnoreCase("%2e.")
                    || input.equalsIgnoreCase(".%2e")
                    || input.equalsIgnoreCase("%2e%2e");
        }

        /**
         * Removes a path segment. When this method returns the last segment is always "", which means
         * the encoded path will have a trailing '/'.
         *
         * <p>Popping "/a/b/c/" yields "/a/b/". In this case the list of path segments goes from ["a",
         * "b", "c", ""] to ["a", "b", ""].
         *
         * <p>Popping "/a/b/c" also yields "/a/b/". The list of path segments goes from ["a", "b", "c"]
         * to ["a", "b", ""].
         */
        private void pop() {
            String removed = encodedPathSegments.remove(encodedPathSegments.size() - 1);

            // Make sure the path ends with a '/' by either adding an empty string or clearing a segment.
            if (removed.isEmpty() && !encodedPathSegments.isEmpty()) {
                encodedPathSegments.set(encodedPathSegments.size() - 1, "");
            } else {
                encodedPathSegments.add("");
            }
        }

        /**
         * Returns the index of the ':' in {@code input} that is after scheme characters. Returns -1 if
         * {@code input} does not have a scheme that starts at {@code pos}.
         */
        private static int schemeDelimiterOffset(String input, int pos, int limit) {
            if (limit - pos < 2) return -1;

            char c0 = input.charAt(pos);
            if ((c0 < 'a' || c0 > 'z') && (c0 < 'A' || c0 > 'Z'))
                return -1; // Not a scheme start char.

            for (int i = pos + 1; i < limit; i++) {
                char c = input.charAt(i);

                if ((c >= 'a' && c <= 'z')
                        || (c >= 'A' && c <= 'Z')
                        || (c >= '0' && c <= '9')
                        || c == '+'
                        || c == '-'
                        || c == '.') {
                    continue; // Scheme character. Keep going.
                } else if (c == ':') {
                    return i; // Scheme prefix!
                } else {
                    return -1; // Non-scheme character before the first ':'.
                }
            }

            return -1; // No ':'; doesn't start with a scheme.
        }

        /**
         * Returns the number of '/' and '\' slashes in {@code input}, starting at {@code pos}.
         */
        private static int slashCount(String input, int pos, int limit) {
            int slashCount = 0;
            while (pos < limit) {
                char c = input.charAt(pos);
                if (c == '\\' || c == '/') {
                    slashCount++;
                    pos++;
                } else {
                    break;
                }
            }
            return slashCount;
        }

        /**
         * Finds the first ':' in {@code input}, skipping characters between square braces "[...]".
         */
        private static int portColonOffset(String input, int pos, int limit) {
            for (int i = pos; i < limit; i++) {
                switch (input.charAt(i)) {
                    case '[':
                        while (++i < limit) {
                            if (input.charAt(i) == ']') break;
                        }
                        break;
                    case ':':
                        return i;
                }
            }
            return limit; // No colon.
        }

        private static String canonicalizeHost(String input, int pos, int limit) {
            // Start by percent decoding the host. The WHATWG spec suggests doing this only after we've
            // checked for IPv6 square braces. But Chrome does it first, and that's more lenient.
            String percentDecoded = percentDecode(input, pos, limit, false);

            // If the input contains a :, it’s an IPv6 address.
            if (percentDecoded.contains(":")) {
                // If the input is encased in square braces "[...]", drop 'em.
                InetAddress inetAddress = percentDecoded.startsWith("[") && percentDecoded.endsWith("]")
                        ? decodeIpv6(percentDecoded, 1, percentDecoded.length() - 1)
                        : decodeIpv6(percentDecoded, 0, percentDecoded.length());
                if (inetAddress == null) return null;
                byte[] address = inetAddress.getAddress();
                if (address.length == 16) return inet6AddressToAscii(address);
                throw new AssertionError();
            }

            return domainToAscii(percentDecoded);
        }

        /**
         * Decodes an IPv6 address like 1111:2222:3333:4444:5555:6666:7777:8888 or ::1.
         */
        private static InetAddress decodeIpv6(String input, int pos, int limit) {
            byte[] address = new byte[16];
            int b = 0;
            int compress = -1;
            int groupOffset = -1;

            for (int i = pos; i < limit; ) {
                if (b == address.length) return null; // Too many groups.

                // Read a delimiter.
                if (i + 2 <= limit && input.regionMatches(i, "::", 0, 2)) {
                    // Compression "::" delimiter, which is anywhere in the input, including its prefix.
                    if (compress != -1) return null; // Multiple "::" delimiters.
                    i += 2;
                    b += 2;
                    compress = b;
                    if (i == limit) break;
                } else if (b != 0) {
                    // Group separator ":" delimiter.
                    if (input.regionMatches(i, ":", 0, 1)) {
                        i++;
                    } else if (input.regionMatches(i, ".", 0, 1)) {
                        // If we see a '.', rewind to the beginning of the previous group and parse as IPv4.
                        if (!decodeIpv4Suffix(input, groupOffset, limit, address, b - 2))
                            return null;
                        b += 2; // We rewound two bytes and then added four.
                        break;
                    } else {
                        return null; // Wrong delimiter.
                    }
                }

                // Read a group, one to four hex digits.
                int value = 0;
                groupOffset = i;
                for (; i < limit; i++) {
                    char c = input.charAt(i);
                    int hexDigit = decodeHexDigit(c);
                    if (hexDigit == -1) break;
                    value = (value << 4) + hexDigit;
                }
                int groupLength = i - groupOffset;
                if (groupLength == 0 || groupLength > 4) return null; // Group is the wrong size.

                // We've successfully read a group. Assign its value to our byte array.
                address[b++] = (byte) ((value >>> 8) & 0xff);
                address[b++] = (byte) (value & 0xff);
            }

            // All done. If compression happened, we need to move bytes to the right place in the
            // address. Here's a sample:
            //
            //      input: "1111:2222:3333::7777:8888"
            //     before: { 11, 11, 22, 22, 33, 33, 00, 00, 77, 77, 88, 88, 00, 00, 00, 00  }
            //   compress: 6
            //          b: 10
            //      after: { 11, 11, 22, 22, 33, 33, 00, 00, 00, 00, 00, 00, 77, 77, 88, 88 }
            //
            if (b != address.length) {
                if (compress == -1)
                    return null; // Address didn't have compression or enough groups.
                System.arraycopy(address, compress, address, address.length - (b - compress), b - compress);
                Arrays.fill(address, compress, compress + (address.length - b), (byte) 0);
            }

            try {
                return InetAddress.getByAddress(address);
            } catch (UnknownHostException e) {
                throw new AssertionError();
            }
        }

        /**
         * Decodes an IPv4 address suffix of an IPv6 address, like 1111::5555:6666:192.168.0.1.
         */
        private static boolean decodeIpv4Suffix(
                String input, int pos, int limit, byte[] address, int addressOffset) {
            int b = addressOffset;

            for (int i = pos; i < limit; ) {
                if (b == address.length) return false; // Too many groups.

                // Read a delimiter.
                if (b != addressOffset) {
                    if (input.charAt(i) != '.') return false; // Wrong delimiter.
                    i++;
                }

                // Read 1 or more decimal digits for a value in 0..255.
                int value = 0;
                int groupOffset = i;
                for (; i < limit; i++) {
                    char c = input.charAt(i);
                    if (c < '0' || c > '9') break;
                    if (value == 0 && groupOffset != i)
                        return false; // Reject unnecessary leading '0's.
                    value = (value * 10) + c - '0';
                    if (value > 255) return false; // Value out of range.
                }
                int groupLength = i - groupOffset;
                if (groupLength == 0) return false; // No digits.

                // We've successfully read a byte.
                address[b++] = (byte) value;
            }

            if (b != addressOffset + 4) return false; // Too few groups. We wanted exactly four.
            return true; // Success.
        }

        private static String inet6AddressToAscii(byte[] address) {
            // Go through the address looking for the longest run of 0s. Each group is 2-bytes.
            int longestRunOffset = -1;
            int longestRunLength = 0;
            for (int i = 0; i < address.length; i += 2) {
                int currentRunOffset = i;
                while (i < 16 && address[i] == 0 && address[i + 1] == 0) {
                    i += 2;
                }
                int currentRunLength = i - currentRunOffset;
                if (currentRunLength > longestRunLength) {
                    longestRunOffset = currentRunOffset;
                    longestRunLength = currentRunLength;
                }
            }

            // Emit each 2-byte group in hex, separated by ':'. The longest run of zeroes is "::".
            Buffer result = new Buffer();
            for (int i = 0; i < address.length; ) {
                if (i == longestRunOffset) {
                    result.writeByte(':');
                    i += longestRunLength;
                    if (i == 16) result.writeByte(':');
                } else {
                    if (i > 0) result.writeByte(':');
                    int group = (address[i] & 0xff) << 8 | address[i + 1] & 0xff;
                    result.writeHexadecimalUnsignedLong(group);
                    i += 2;
                }
            }
            return result.readUtf8();
        }

        private static int parsePort(String input, int pos, int limit) {
            try {
                // Canonicalize the port string to skip '\n' etc.
                String portString = canonicalize(input, pos, limit, "", false, false, false, true);
                int i = Integer.parseInt(portString);
                if (i > 0 && i <= 65535) return i;
                return -1;
            } catch (NumberFormatException e) {
                return -1; // Invalid port.
            }
        }
    }

    static String percentDecode(String encoded, boolean plusIsSpace) {
        return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
    }

    private List<String> percentDecode(List<String> list, boolean plusIsSpace) {
        List<String> result = new ArrayList<>(list.size());
        for (String s : list) {
            result.add(s != null ? percentDecode(s, plusIsSpace) : null);
        }
        return Collections.unmodifiableList(result);
    }

    static String percentDecode(String encoded, int pos, int limit, boolean plusIsSpace) {
        for (int i = pos; i < limit; i++) {
            char c = encoded.charAt(i);
            if (c == '%' || (c == '+' && plusIsSpace)) {
                // Slow path: the character at i requires decoding!
                Buffer out = new Buffer();
                out.writeUtf8(encoded, pos, i);
                percentDecode(out, encoded, i, limit, plusIsSpace);
                return out.readUtf8();
            }
        }

        // Fast path: no characters in [pos..limit) required decoding.
        return encoded.substring(pos, limit);
    }

    static void percentDecode(Buffer out, String encoded, int pos, int limit, boolean plusIsSpace) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = encoded.codePointAt(i);
            if (codePoint == '%' && i + 2 < limit) {
                int d1 = decodeHexDigit(encoded.charAt(i + 1));
                int d2 = decodeHexDigit(encoded.charAt(i + 2));
                if (d1 != -1 && d2 != -1) {
                    out.writeByte((d1 << 4) + d2);
                    i += 2;
                    continue;
                }
            } else if (codePoint == '+' && plusIsSpace) {
                out.writeByte(' ');
                continue;
            }
            out.writeUtf8CodePoint(codePoint);
        }
    }

    static boolean percentEncoded(String encoded, int pos, int limit) {
        return pos + 2 < limit
                && encoded.charAt(pos) == '%'
                && decodeHexDigit(encoded.charAt(pos + 1)) != -1
                && decodeHexDigit(encoded.charAt(pos + 2)) != -1;
    }

    static int decodeHexDigit(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        return -1;
    }

    /**
     * Returns a substring of {@code input} on the range {@code [pos..limit)} with the following
     * transformations:
     * <ul>
     * <li>Tabs, newlines, form feeds and carriage returns are skipped.
     * <li>In queries, ' ' is encoded to '+' and '+' is encoded to "%2B".
     * <li>Characters in {@code encodeSet} are percent-encoded.
     * <li>Control characters and non-ASCII characters are percent-encoded.
     * <li>All other characters are copied without transformation.
     * </ul>
     *
     * @param alreadyEncoded true to leave '%' as-is; false to convert it to '%25'.
     * @param strict         true to encode '%' if it is not the prefix of a valid percent encoding.
     * @param plusIsSpace    true to encode '+' as "%2B" if it is not already encoded.
     * @param asciiOnly      true to encode all non-ASCII codepoints.
     */
    static String canonicalize(String input, int pos, int limit, String encodeSet,
                               boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (codePoint < 0x20
                    || codePoint == 0x7f
                    || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))
                    || codePoint == '+' && plusIsSpace) {
                // Slow path: the character at i requires encoding!
                Buffer out = new Buffer();
                out.writeUtf8(input, pos, i);
                canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace,
                        asciiOnly);
                return out.readUtf8();
            }
        }

        // Fast path: no characters in [pos..limit) required encoding.
        return input.substring(pos, limit);
    }

    static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet,
                             boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
        Buffer utf8Buffer = null; // Lazily allocated.
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (alreadyEncoded
                    && (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
                // Skip this character.
            } else if (codePoint == '+' && plusIsSpace) {
                // Encode '+' as '%2B' since we permit ' ' to be encoded as either '+' or '%20'.
                out.writeUtf8(alreadyEncoded ? "+" : "%2B");
            } else if (codePoint < 0x20
                    || codePoint == 0x7f
                    || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))) {
                // Percent encode this character.
                if (utf8Buffer == null) {
                    utf8Buffer = new Buffer();
                }
                utf8Buffer.writeUtf8CodePoint(codePoint);
                while (!utf8Buffer.exhausted()) {
                    int b = utf8Buffer.readByte() & 0xff;
                    out.writeByte('%');
                    out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
                    out.writeByte(HEX_DIGITS[b & 0xf]);
                }
            } else {
                // This character doesn't need encoding. Just copy it over.
                out.writeUtf8CodePoint(codePoint);
            }
        }
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict,
                               boolean plusIsSpace, boolean asciiOnly) {
        return canonicalize(
                input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly);
    }


    private static int skipLeadingAsciiWhitespace(String input, int pos, int limit) {
        for (int i = pos; i < limit; i++) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    continue;
                default:
                    return i;
            }
        }
        return limit;
    }

    /**
     * Decrements {@code limit} until {@code input[limit - 1]} is not ASCII whitespace. Stops at
     * {@code pos}.
     */
    private static int skipTrailingAsciiWhitespace(String input, int pos, int limit) {
        for (int i = limit - 1; i >= pos; i--) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    continue;
                default:
                    return i + 1;
            }
        }
        return pos;
    }


    /**
     * Returns the index of the first character in {@code input} that contains a character in {@code
     * delimiters}. Returns limit if there is no such character.
     */
    private static int delimiterOffset(String input, int pos, int limit, String delimiters) {
        for (int i = pos; i < limit; i++) {
            if (delimiters.indexOf(input.charAt(i)) != -1) return i;
        }
        return limit;
    }

    /**
     * Returns the index of the first character in {@code input} that is {@code delimiter}. Returns
     * limit if there is no such character.
     */
    private static int delimiterOffset(String input, int pos, int limit, char delimiter) {
        for (int i = pos; i < limit; i++) {
            if (input.charAt(i) == delimiter) return i;
        }
        return limit;
    }

    /**
     * Performs IDN ToASCII encoding and canonicalize the result to lowercase. e.g. This converts
     * {@code ☃.net} to {@code xn--n3h.net}, and {@code WwW.GoOgLe.cOm} to {@code www.google.com}.
     * {@code null} will be returned if the input cannot be ToASCII encoded or if the result
     * contains unsupported ASCII characters.
     */
    private static String domainToAscii(String input) {
        try {
            String result = IDN.toASCII(input).toLowerCase(Locale.US);
            if (result.isEmpty()) return null;

            // Confirm that the IDN ToASCII result doesn't contain any illegal characters.
            if (containsInvalidHostnameAsciiCodes(result)) {
                return null;
            }
            // TODO: implement all label limits.
            return result;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean containsInvalidHostnameAsciiCodes(String hostnameAscii) {
        for (int i = 0; i < hostnameAscii.length(); i++) {
            char c = hostnameAscii.charAt(i);
            // The WHATWG Host parsing rules accepts some character codes which are invalid by
            // definition for OkHttp's host header checks (and the WHATWG Host syntax definition). Here
            // we rule out characters that would cause problems in host headers.
            if (c <= '\u001f' || c >= '\u007f') {
                return true;
            }
            // Check for the characters mentioned in the WHATWG Host parsing spec:
            // U+0000, U+0009, U+000A, U+000D, U+0020, "#", "%", "/", ":", "?", "@", "[", "\", and "]"
            // (excluding the characters covered above).
            if (" #%/:?@[\\]".indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

}
