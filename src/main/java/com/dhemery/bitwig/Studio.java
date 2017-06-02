package com.dhemery.bitwig;

import java.util.Arrays;
import java.util.stream.Stream;

public interface Studio {
    String STATUS_CODE_MASK_FORMAT = "%x?????";

    static String[] noteInputMasks(int... types) {
        return Arrays.stream(types)
                .map(statusByte -> statusByte >>> 4)
                .mapToObj(statusNibble -> String.format(STATUS_CODE_MASK_FORMAT, statusNibble))
                .toArray(String[]::new);
    }
}
