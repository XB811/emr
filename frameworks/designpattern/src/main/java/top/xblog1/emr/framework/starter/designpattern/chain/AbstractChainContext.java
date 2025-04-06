package top.xblog1.emr.framework.starter.designpattern.chain;


import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import top.xblog1.emr.framework.starter.base.ApplicationContextHolder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽象责任链上下文
 */
public final class AbstractChainContext<T> implements CommandLineRunner {

    //mark对应的handler封装
    private final Map<String, List<AbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();

    /**
     * 责任链组件执行
     *
     * @param mark         责任链组件标识
     * @param requestParam 请求参数
     */
    public void handler(String mark, T requestParam) {
        //根据mark获得同一类的handler
        List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(mark);
        if (CollectionUtils.isEmpty(abstractChainHandlers)) {
            throw new RuntimeException(String.format("[%s] Chain of Responsibility ID is undefined.", mark));
        }
        //执行责任链中的handler
        abstractChainHandlers.forEach(each -> each.handler(requestParam));
    }
    /**
    * 继承自CommandLineRunner，必须实现的方法
    * 初始化时，将所有的责任链都装入该类中
    * @param args
    * @return
    */
    @Override
    public void run(String... args) throws Exception {
        /**
        * 获取所有继承了 {@link AbstractChainHandler}的责任链钩子
        */
        Map<String, AbstractChainHandler> chainFilterMap = ApplicationContextHolder
                .getBeansOfType(AbstractChainHandler.class);
        /**
        * 根据mark将责任链钩子装入 {@link AbstractChainContext#abstractChainHandlerContainer}中
        */
        chainFilterMap.forEach((beanName, bean) -> {
            List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(bean.mark());
            if (CollectionUtils.isEmpty(abstractChainHandlers)) {
                abstractChainHandlers = new ArrayList();
            }
            abstractChainHandlers.add(bean);
            //对于相同mark的handle按照order进行重新排序
            List<AbstractChainHandler> actualAbstractChainHandlers = abstractChainHandlers.stream()
                    .sorted(Comparator.comparing(Ordered::getOrder))
                    .collect(Collectors.toList());
            abstractChainHandlerContainer.put(bean.mark(), actualAbstractChainHandlers);
        });
    }
}
