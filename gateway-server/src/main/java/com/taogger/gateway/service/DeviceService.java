package com.taogger.gateway.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import yxd.kj.app.server.gateway.utils.Device;
import yxd.kj.app.server.gateway.utils.DevicePlatform;
import yxd.kj.app.server.gateway.utils.DeviceType;
import yxd.kj.app.server.gateway.utils.LiteDevice;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 设备信息获取--不精确，手机模拟器识别不了
 * @author taogger
 * @date 2022/8/10 15:21
 */
@Service
public class DeviceService {

    private final List<String> mobileUserAgentPrefixes = new ArrayList<String>();

    private final List<String> mobileUserAgentKeywords = new ArrayList<String>();

    private final List<String> tabletUserAgentKeywords = new ArrayList<String>();

    private final List<String> normalUserAgentKeywords = new ArrayList<String>();

    public List<String> getMobileUserAgentPrefixes() {
        return mobileUserAgentPrefixes;
    }

    public List<String> getMobileUserAgentKeywords() {
        return mobileUserAgentKeywords;
    }

    public List<String> getTabletUserAgentKeywords() {
        return tabletUserAgentKeywords;
    }

    public List<String> getNormalUserAgentKeywords() {
        return normalUserAgentKeywords;
    }

    public Device getDevice(ServerHttpRequest request) {
        String userAgent = request.getHeaders().getFirst("User-Agent");
        // UserAgent keyword detection of Normal devices
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            for (String keyword : normalUserAgentKeywords) {
                if (userAgent.contains(keyword)) {
                    return resolveFallback();
                }
            }
        }
        // UserAgent keyword detection of Tablet devices
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            // Android special case
            if (userAgent.contains("android") && !userAgent.contains("mobile")) {
                return resolveWithPlatform(DeviceType.TABLET, DevicePlatform.ANDROID);
            }
            // Apple special case
            if (userAgent.contains("ipad")) {
                return resolveWithPlatform(DeviceType.TABLET, DevicePlatform.IOS);
            }
            // Kindle Fire special case
            if (userAgent.contains("silk") && !userAgent.contains("mobile")) {
                return resolveWithPlatform(DeviceType.TABLET, DevicePlatform.UNKNOWN);
            }
            for (String keyword : tabletUserAgentKeywords) {
                if (userAgent.contains(keyword)) {
                    return resolveWithPlatform(DeviceType.TABLET, DevicePlatform.UNKNOWN);
                }
            }
        }
        // UAProf detection
        if (request.getHeaders().getFirst("x-wap-profile") != null || request.getHeaders().getFirst("Profile") != null) {
            if (userAgent != null) {
                // Android special case
                if (userAgent.contains("android")) {
                    return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.ANDROID);
                }
                // Apple special case
                if (userAgent.contains("iphone") || userAgent.contains("ipod") || userAgent.contains("ipad")) {
                    return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.IOS);
                }
            }
            return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.UNKNOWN);
        }
        // User-Agent prefix detection
        if (userAgent != null && userAgent.length() >= 4) {
            String prefix = userAgent.substring(0, 4).toLowerCase();
            if (mobileUserAgentPrefixes.contains(prefix)) {
                return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.UNKNOWN);
            }
        }
        // Accept-header based detection
        String accept = request.getHeaders().getFirst("Accept");
        if (accept != null && accept.contains("wap")) {
            return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.UNKNOWN);
        }
        // UserAgent keyword detection for Mobile devices
        if (userAgent != null) {
            // Android special case
            if (userAgent.contains("android")) {
                return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.ANDROID);
            }
            // Apple special case
            if (userAgent.contains("iphone") || userAgent.contains("ipod") || userAgent.contains("ipad")) {
                return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.IOS);
            }
            for (String keyword : mobileUserAgentKeywords) {
                if (userAgent.contains(keyword)) {
                    return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.UNKNOWN);
                }
            }
        }
        // OperaMini special case
        @SuppressWarnings("rawtypes")
        HttpHeaders headers = request.getHeaders();
        if (headers.containsKey("OperaMini")) {
            /*return LiteDevice.MOBILE_INSTANCE;*/
            return resolveWithPlatform(DeviceType.MOBILE, DevicePlatform.UNKNOWN);
        }
        return resolveFallback();
    }

    protected Device resolveFallback() {
        return LiteDevice.NORMAL_INSTANCE;
    }

    protected Device resolveWithPlatform(DeviceType deviceType, DevicePlatform devicePlatform) {
        return LiteDevice.from(deviceType, devicePlatform);
    }

    /**
     * Initialize this device resolver implementation. Registers the known set of device
     * signature strings. Subclasses may override to register additional strings.
     */
    @PostConstruct
    public void init() {
        getMobileUserAgentPrefixes().addAll(
                Arrays.asList(KNOWN_MOBILE_USER_AGENT_PREFIXES));
        getMobileUserAgentKeywords().addAll(
                Arrays.asList(KNOWN_MOBILE_USER_AGENT_KEYWORDS));
        getTabletUserAgentKeywords().addAll(
                Arrays.asList(KNOWN_TABLET_USER_AGENT_KEYWORDS));
    }

    private static final String[] KNOWN_MOBILE_USER_AGENT_PREFIXES = new String[] {
            "w3c ", "w3c-", "acs-", "alav", "alca", "amoi", "avan", "benq", "bird",
            "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric",
            "hipt", "htc_", "inno", "ipaq", "ipod", "jigs", "kddi", "keji", "leno",
            "lg-c", "lg-d", "lg-g", "lge-", "lg/u", "maui", "maxo", "midp", "mits",
            "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki", "palm",
            "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage", "sams",
            "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",
            "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh",
            "tsm-", "upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp",
            "wapr", "webc", "winw", "winw", "xda ", "xda-" };

    private static final String[] KNOWN_MOBILE_USER_AGENT_KEYWORDS = new String[] {
            "blackberry", "webos", "ipod", "lge vx", "midp", "maemo", "mmp", "mobile",
            "netfront", "hiptop", "nintendo DS", "novarra", "openweb", "opera mobi",
            "opera mini", "palm", "psp", "phone", "smartphone", "symbian", "up.browser",
            "up.link", "wap", "windows ce" };

    private static final String[] KNOWN_TABLET_USER_AGENT_KEYWORDS = new String[] {
            "ipad", "playbook", "hp-tablet", "kindle" };
}
