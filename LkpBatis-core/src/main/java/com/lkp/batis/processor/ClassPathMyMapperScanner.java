package com.lkp.batis.processor;

import com.lkp.batis.annotation.MyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Target;
import java.util.Objects;
import java.util.Set;
@Slf4j
public class ClassPathMyMapperScanner extends ClassPathBeanDefinitionScanner {
    private final ClassLoader classLoader;
    public ClassPathMyMapperScanner(BeanDefinitionRegistry registry, ClassLoader classLoader) {
        super(registry, false);
        this.classLoader=classLoader;
    }
    public void registerFilters(){
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(MyMapper.class);
        this.addIncludeFilter(annotationTypeFilter);
        log.info("mymapper filter registered");
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        if(beanDefinition.getMetadata().isInterface()){
            try {
                Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), classLoader);
                return !target.isAnnotation();
            }catch (Exception e){
                log.error("load class :"+ beanDefinition.getMetadata().getClassName() +"error:"+e);
            }
        }
        return false;
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if(beanDefinitionHolders.isEmpty()){
            log.warn("no MyMapper annotation!check your coding");
        }else{
            log.info("get "+beanDefinitionHolders.size()+" interfaces with MyMapper");
            postBeanDefinitions(beanDefinitionHolders);
        }
        return beanDefinitionHolders;
    }

    private void postBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitionHolders) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            if (log.isDebugEnabled()){
                log.debug("Creating MyMapper with name '"+holder.getBeanName()+"' and '"+definition.getBeanClassName()+"' interface");
            }
            definition.getConstructorArgumentValues().addGenericArgumentValue(Objects.requireNonNull(definition.getBeanClassName()));
            //把所有被mymapper标注的接口都改沉MyMapperFactoryBean这个类
            definition.setBeanClass(MyMapperFactoryBean.class);
        }
    }
}
