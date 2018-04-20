package com.rbkmoney.magista;

import com.rbkmoney.magista.util.TypeUtil;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TypeUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void testUUIDGreaterThat14() {
        TypeUtil.toUUID("vfregoergemorg!");
    }

    @Test
    public void testV4UUID() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        assertEquals(uuid, TypeUtil.toUUID(uuidString));
        assertEquals(uuidString, TypeUtil.fromUUID(uuid));
    }

    @Test
    public void testV3UUID() {
        String value = "TEST";
        UUID uuid = TypeUtil.toUUID(value);
        assertEquals("54455354-0000-3000-8000-000000000000", uuid.toString());
        assertEquals(value, TypeUtil.fromUUID(uuid));

        value = "2";
        uuid = TypeUtil.toUUID(value);
        assertEquals("32000000-0000-3000-8000-000000000000", uuid.toString());
        assertEquals(value, TypeUtil.fromUUID(uuid));

        value = "vfregoergemorg";
        uuid = TypeUtil.toUUID(value);
        assertEquals("76667265-676f-3065-8072-67656d6f7267", uuid.toString());
        assertEquals(value, TypeUtil.fromUUID(uuid));
    }

}
