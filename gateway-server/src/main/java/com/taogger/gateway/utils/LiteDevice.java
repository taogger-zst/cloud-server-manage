package com.taogger.gateway.utils;


/**
 * @author taogger
 * @date 2022/8/10 15:32
 */
public class LiteDevice implements Device {

    public static final LiteDevice NORMAL_INSTANCE = new LiteDevice(DeviceType.NORMAL);

    public static final LiteDevice MOBILE_INSTANCE = new LiteDevice(DeviceType.MOBILE);

    public static final LiteDevice TABLET_INSTANCE = new LiteDevice(DeviceType.TABLET);

    private final DeviceType deviceType;

    private final DevicePlatform devicePlatform;

    public DevicePlatform getDevicePlatform() {
        return this.devicePlatform;
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    /**
     * Creates a LiteDevice with DeviceType of NORMAL and DevicePlatform UNKNOWN
     */
    public LiteDevice() {
        this(DeviceType.NORMAL, DevicePlatform.UNKNOWN);
    }

    /**
     * Creates a LiteDevice with DevicePlatform UNKNOWN
     * @param deviceType the type of device i.e. NORMAL, MOBILE, TABLET
     */
    public LiteDevice(DeviceType deviceType) {
        this(deviceType, DevicePlatform.UNKNOWN);
    }

    /**
     * Creates a LiteDevice
     * @param deviceType the type of device i.e. NORMAL, MOBILE, TABLET
     * @param devicePlatform the platform of device, i.e. IOS or ANDROID
     */
    public LiteDevice(DeviceType deviceType, DevicePlatform devicePlatform) {
        this.deviceType = deviceType;
        this.devicePlatform = devicePlatform;
    }

    public boolean isNormal() {
        return this.deviceType == DeviceType.NORMAL;
    }

    public boolean isMobile() {
        return this.deviceType == DeviceType.MOBILE;
    }

    public boolean isTablet() {
        return this.deviceType == DeviceType.TABLET;
    }

    public static Device from(DeviceType deviceType, DevicePlatform devicePlatform) {
        return new LiteDevice(deviceType, devicePlatform);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[LiteDevice ");
        builder.append("type").append("=").append(this.deviceType);
        builder.append("]");
        return builder.toString();
    }
}