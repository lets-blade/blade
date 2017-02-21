/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.multipart;

import com.blade.kit.IOKit;
import com.blade.mvc.handler.MultipartHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Multipart
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class Multipart {

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static final String CONTENT_LENGTH = "Content-Length";

    public static final String FORM_DATA = "form-data";

    public static final String ATTACHMENT = "attachment";

    public static final String MULTIPART = "multipart/";

    public static final String MULTIPART_MIXED = "multipart/mixed";

    public static boolean isMultipartContent(HttpServletRequest request) {
        if (!"post".equals(request.getMethod().toLowerCase())) {
            return false;
        }

        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        if (contentType.toLowerCase().startsWith(MULTIPART)) {
            return true;
        }

        return false;
    }

    public void parse(HttpServletRequest request, MultipartHandler partHandler) throws IOException, MultipartException {
        if (!isMultipartContent(request)) {
            throw new MultipartException("Not a multipart content. The HTTP method should be 'POST' and the " +
                    "Content-Type 'multipart/form-data' or 'multipart/mixed'.");
        }

        InputStream inputStream = request.getInputStream();

        String contentType = request.getContentType();
        String charEncoding = request.getCharacterEncoding();

        byte[] boundary = getBoundary(contentType);
        if (boundary == null) {
            throw new MultipartException("the request was rejected because no multipart boundary was found");
        }

        // create a multipart reader
        MultipartReader multipartReader = new MultipartReader(inputStream, boundary);
        multipartReader.setHeaderEncoding(charEncoding);

        String currentFieldName = null;
        boolean skipPreamble = true;

        for (; ; ) {
            boolean nextPart;
            if (skipPreamble) {
                nextPart = multipartReader.skipPreamble();
            } else {
                nextPart = multipartReader.readBoundary();
            }
            if (!nextPart) {
                if (currentFieldName == null) {
                    // outer multipart terminated -> no more data
                    return;
                }
                // inner multipart terminated -> return to parsing the outer
                multipartReader.setBoundary(boundary);
                currentFieldName = null;
                continue;
            }

            String headersString = multipartReader.readHeaders();
            Map<String, String> headers = getHeadersMap(headersString);

            if (currentFieldName == null) {

                // we're parsing the outer multipart
                String fieldName = getFieldName(headers.get(CONTENT_DISPOSITION));
                if (fieldName != null) {

                    String partContentType = headers.get(CONTENT_TYPE);
                    if (partContentType != null && partContentType.toLowerCase().startsWith(MULTIPART_MIXED)) {

                        // multiple files associated with this field name
                        currentFieldName = fieldName;
                        multipartReader.setBoundary(getBoundary(partContentType));
                        skipPreamble = true;

                        continue;
                    }

                    String fileName = getFileName(headers.get(CONTENT_DISPOSITION));
                    if (fileName == null) {
                        // call the part handler
                        String value = IOKit.toString(multipartReader.newInputStream());
                        partHandler.handleFormItem(fieldName, value);
                    } else {

                        // create the temp file
                        File tempFile = createTempFile(multipartReader);

                        // call the part handler
                        FileItem fileItem = new FileItem(fieldName, fileName, partContentType, tempFile.length(), tempFile, headers);
                        partHandler.handleFileItem(fieldName, fileItem);
                    }

                    continue;
                }
            } else {
                String fileName = getFileName(headers.get(CONTENT_DISPOSITION));
                String partContentType = headers.get(CONTENT_TYPE);
                if (fileName != null) {

                    // create the temp file
                    File tempFile = createTempFile(multipartReader);

                    // call the part handler
                    FileItem fileItem = new FileItem(currentFieldName, fileName, partContentType, tempFile.length(),
                            tempFile, headers);
                    partHandler.handleFileItem(currentFieldName, fileItem);
                    continue;
                }
            }
            multipartReader.discardBodyData();
        }

    }

    private File createTempFile(MultipartReader multipartReader) throws IOException {
        File tempFile = File.createTempFile("com.blade.file_", null);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempFile);
            copy(multipartReader.newInputStream(), outputStream);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
            }
        }

        return tempFile;
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    private long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    protected Map<String, String> getHeadersMap(String headerPart) {
        final int len = headerPart.length();
        final Map<String, String> headers = new HashMap<String, String>();

        int start = 0;
        for (; ; ) {
            int end = parseEndOfLine(headerPart, start);
            if (start == end) {
                break;
            }
            String header = headerPart.substring(start, end);
            start = end + 2;
            while (start < len) {
                int nonWs = start;
                while (nonWs < len) {
                    char c = headerPart.charAt(nonWs);
                    if (c != ' ' && c != '\t') {
                        break;
                    }
                    ++nonWs;
                }
                if (nonWs == start) {
                    break;
                }
                // continuation line found
                end = parseEndOfLine(headerPart, nonWs);
                header += " " + headerPart.substring(nonWs, end);
                start = end + 2;
            }

            // parse header line
            final int colonOffset = header.indexOf(':');
            if (colonOffset == -1) {
                // this header line is malformed, skip it.
                continue;
            }
            String headerName = header.substring(0, colonOffset).trim();
            String headerValue = header.substring(header.indexOf(':') + 1).trim();

            if (headers.containsKey(headerName)) {
                headers.put(headerName, headers.get(headerName) + "," + headerValue);
            } else {
                headers.put(headerName, headerValue);
            }
        }

        return headers;
    }

    private int parseEndOfLine(String headerPart, int end) {
        int index = end;
        for (; ; ) {
            int offset = headerPart.indexOf('\r', index);
            if (offset == -1 || offset + 1 >= headerPart.length()) {
                throw new IllegalStateException("Expected headers to be terminated by an empty line.");
            }
            if (headerPart.charAt(offset + 1) == '\n') {
                return offset;
            }
            index = offset + 1;
        }
    }

    private String getFieldName(String contentDisposition) {
        String fieldName = null;

        if (contentDisposition != null && contentDisposition.toLowerCase().startsWith(FORM_DATA)) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);

            // parameter parser can handle null input
            Map<String, String> params = parser.parse(contentDisposition, ';');
            fieldName = (String) params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }

        return fieldName;
    }

    protected byte[] getBoundary(String contentType) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        // Parameter parser can handle null input
        Map<String, String> params = parser.parse(contentType, new char[]{';', ','});
        String boundaryStr = (String) params.get("boundary");

        if (boundaryStr == null) {
            return null;
        }

        byte[] boundary;
        try {
            boundary = boundaryStr.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            boundary = boundaryStr.getBytes();
        }
        return boundary;
    }

    private String getFileName(String contentDisposition) {
        String fileName = null;

        if (contentDisposition != null) {
            String cdl = contentDisposition.toLowerCase();

            if (cdl.startsWith(FORM_DATA) || cdl.startsWith(ATTACHMENT)) {

                ParameterParser parser = new ParameterParser();
                parser.setLowerCaseNames(true);

                // parameter parser can handle null input
                Map<String, String> params = parser.parse(contentDisposition, ';');
                if (params.containsKey("filename")) {
                    fileName = (String) params.get("filename");
                    if (fileName != null) {
                        fileName = fileName.trim();
                    } else {
                        // even if there is no value, the parameter is present,
                        // so we return an empty file name rather than no file
                        // name.
                        fileName = "";
                    }
                }
            }
        }

        return fileName;
    }

}
