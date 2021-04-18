package com.ihouse.backend.constants;
import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.Map;

@Data
public class RentValueBlock {
    /**
     * 价格区间定义
     */
    public static final Map<String, RentValueBlock> PRICE_BLOCK;

    /**
     * 面积区间定义
     */
    public static final Map<String, RentValueBlock> AREA_BLOCK;

    /**
     * 无限制区间
     */
    public static final RentValueBlock ALL = new RentValueBlock("*", -1, -1);

    static {
        PRICE_BLOCK = ImmutableMap.<String, RentValueBlock>builder()
                .put("*-5000", new RentValueBlock("*-5000", -1, 5000))
                .put("5000-10000", new RentValueBlock("5000-10000", 5000, 10000))
                .put("10000-*", new RentValueBlock("10000-*", 10000, -1))
                .build();

        AREA_BLOCK = ImmutableMap.<String, RentValueBlock>builder()
                .put("*-60", new RentValueBlock("*-60", -1, 60))
                .put("60-100", new RentValueBlock("60-100", 60, 100))
                .put("100-*", new RentValueBlock("100-*", 100, -1))
                .build();
    }

    private String key;
    private int min;
    private int max;

    public RentValueBlock(String key, int min, int max) {
        this.key = key;
        this.min = min;
        this.max = max;
    }

    public static RentValueBlock matchPrice(String key) {
        RentValueBlock block = PRICE_BLOCK.get(key);
        if (block == null) {
            return ALL;
        }
        return block;
    }

    public static RentValueBlock matchArea(String key) {
        RentValueBlock block = AREA_BLOCK.get(key);
        if (block == null) {
            return ALL;
        }
        return block;
    }
}
