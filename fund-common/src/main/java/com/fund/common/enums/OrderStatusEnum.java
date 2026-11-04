package com.fund.common.enums;

/**
 * States shared by purchase and redemption orders.
 */
public enum OrderStatusEnum {
    PENDING_CONFIRMATION("PENDING_CONFIRMATION", "待确认"),
    CONFIRMED("CONFIRMED", "确认成功"),
    CONFIRM_FAILED("CONFIRM_FAILED", "确认失败"),
    REDEEMED("REDEEMED", "已赎回");

    private final String code;
    private final String description;

    OrderStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(OrderStatusEnum target) {
        if (target == null) {
            return false;
        }
        return switch (this) {
            case PENDING_CONFIRMATION -> target == CONFIRMED || target == CONFIRM_FAILED;
            case CONFIRMED -> target == REDEEMED;
            case CONFIRM_FAILED, REDEEMED -> false;
        };
    }

    public static OrderStatusEnum fromCode(String code) {
        for (OrderStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
