package top.xblog1.emr.framework.starter.base.safe;

import org.springframework.beans.factory.InitializingBean;

/**
 * FastJson安全模式，开启后关闭autoType特性 （类型隐式传递）
 */

public class FastJsonSafeMode implements InitializingBean {

    /**
    * 设置系统位FastJson安全模式
    * @return
    */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.setProperty("fastjson2.parser.safeMode", "true");
    }
}

