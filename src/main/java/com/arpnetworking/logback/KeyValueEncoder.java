/**
 * Copyright 2014 Groupon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arpnetworking.logback;

import java.io.StringWriter;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Encoder to take a Steno log event {@link com.arpnetworking.logback.StenoMarker} and convert it to an
 * easier to read key=value output format when running locally.
 *
 * @author Gil Markham (gil at groupon dot com)
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
public class KeyValueEncoder extends BaseLoggingEncoder {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildArrayMessage(
            final ILoggingEvent event,
            final String eventName,
            final String[] keys,
            final Object[] values) {

        final String formatString = buildFormatString(eventName, keys);
        final LoggingEventWrapper eventWrapper = new LoggingEventWrapper(event, formatString, values);
        return layout.doLayout(eventWrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildArrayJsonMessage(
            final ILoggingEvent event,
            final String eventName,
            final String[] keys,
            final String[] jsonValues) {

        final Object[] escapedJsonValues = jsonValues == null ? null : escapeStringValues(jsonValues);
        return buildArrayMessage(event, eventName, keys, escapedJsonValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildMapMessage(
            final ILoggingEvent event,
            final String eventName,
            final Map<String, ? extends Object> map) {

        final String[] keys = map == null ? null : new String[map.size()];
        final Object[] values = map == null ? null : new Object[map.size()];
        if (map != null) {
            int index = 0;
            for (final Map.Entry<String, ? extends Object> entry : map.entrySet()) {
                keys[index] = entry.getKey();
                values[index] = entry.getValue();
                ++index;
            }
        }

        return buildArrayMessage(
            event,
            eventName,
            keys,
            values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildMapJsonMessage(
            final ILoggingEvent event,
            final String eventName,
            final Map<String, String> map) {

        final String[] keys = map == null ? null : new String[map.size()];
        final Object[] values = map == null ? null : new Object[map.size()];
        if (map != null) {
            int index = 0;
            for (final Map.Entry<String, ? extends Object> entry : map.entrySet()) {
                keys[index] = entry.getKey();
                values[index] = entry.getValue();
                ++index;
            }
        }

        return buildArrayMessage(
                event,
                eventName,
                keys,
                values == null ? null : escapeStringValues(values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildObjectMessage(
            final ILoggingEvent event,
            final String eventName,
            final Object data) {

        return buildObjectJsonMessage(
                event,
                eventName,
                data == null ? null : data.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildObjectJsonMessage(
            final ILoggingEvent event,
            final String eventName,
            final String jsonData) {

        return buildArrayMessage(
                event,
                eventName,
                new String[] {"data"},
                escapeStringValues(new Object[] {jsonData}));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    protected String buildJsonMessage(
            final ILoggingEvent event,
            final String eventName,
            final String jsonKey,
            final String json) {

        final String[] keys = new String[]{jsonKey};
        final Object[] values = escapeStringValues(new Object[]{json});
        final String formatString = buildFormatString(eventName, keys);
        final LoggingEventWrapper eventWrapper = new LoggingEventWrapper(event, formatString, values);
        return layout.doLayout(eventWrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildStandardMessage(final ILoggingEvent event) {
        return layout.doLayout(event);
    }

    /**
     * Create a format <code>String</code> compatible with <code>MessageFormatter</code>.
     *
     * @param name The name.
     * @param keys The argument keys.
     * @return The format <code>String</code>.
     */
    protected String buildFormatString(final String name, final String[] keys) {
        final StringWriter stringWriter = new StringWriter();
        stringWriter.append("name=\"").append(name).append("\"");
        if (keys != null && keys.length > 0) {
            for (final String key : keys) {
                stringWriter.append(", ").append(key).append("=\"{}\"");
            }
        }
        return stringWriter.toString();
    }

    /**
     * Escape all <code>String</code> instances.
     *
     * @param values Arguments to encode.
     * @return Encoded arguments.
     */
    protected Object[] escapeStringValues(final Object[] values) {
        final Object[] escapedValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            // Instance of check implies value is not null
            if (value instanceof String) {
                value = ((String) value).replaceAll("\"", "\\\\\"");
            }
            escapedValues[i] = value;
        }
        return escapedValues;
    }
}