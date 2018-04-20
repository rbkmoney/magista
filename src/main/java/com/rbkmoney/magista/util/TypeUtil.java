package com.rbkmoney.magista.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TypeUtil {

    public static <T extends Enum<T>> T toEnumField(String name, Class<T> enumType) {
        return Optional.ofNullable(name)
                .map(value -> Enum.valueOf(enumType, name))
                .orElse(null);
    }

    public static <T extends Enum<T>> List<T> toEnumFields(List<String> names, Class<T> enumType) {
        return Optional.ofNullable(names)
                .map(
                        values -> values.stream()
                                .filter(name -> name != null)
                                .map(name -> toEnumField(name, enumType))
                                .collect(Collectors.toList())
                ).orElse(null);
    }

    public static LocalDateTime toLocalDateTime(TemporalAccessor temporalAccessor) {
        return toLocalDateTime(temporalAccessor, ZoneOffset.UTC);
    }

    public static LocalDateTime toLocalDateTime(TemporalAccessor temporalAccessor, ZoneId zoneId) {
        return Optional.ofNullable(temporalAccessor)
                .map(
                        value -> LocalDateTime.ofInstant(
                                Instant.from(value),
                                zoneId
                        )
                ).orElse(null);
    }

    public static UUID toUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            return toUUIDFromBytes(value.getBytes());
        }
    }

    public static UUID toUUIDFromBytes(byte[] bytes) {
        if (bytes.length > 14) {
            throw new IllegalArgumentException("Byte array length must be less that 14");
        }

        byte[] uuid = new byte[16];
        uuid[6] = 0x30;
        uuid[8] |= 0x80;

        for (int i = 0, j = 0; i < bytes.length; i++, j++) {
            if (j == 6 || j == 8) {
                j++;
            }
            uuid[j] = bytes[i];
        }

        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (uuid[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (uuid[i] & 0xff);
        }

        return new UUID(msb, lsb);
    }

    public static String fromUUID(UUID uuid) {
        return uuid.version() == 3 ? fromUUIDV3(uuid) : uuid.toString();
    }

    public static String fromUUIDV3(UUID uuid) {
        byte[] uuidArray = new byte[14];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();

        int length = 0;
        for (int i = 0, j = 0; i < uuidArray.length; i++, j++) {
            if (j == 6 || j == 8) {
                j++;
            }
            uuidArray[i] = (byte) ((j < 8 ? msb : lsb) >> (56 - (8 * j)) & 0xFF);
            if (uuidArray[i] == 0x00) {
                break;
            }
            length++;
        }

        return new String(uuidArray, 0, length);
    }

}
