package com.lkp.batis.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.boot.autoconfigure.*;

import java.util.List;

@Slf4j
public class AutoConfiguredMapperScannerRegister implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware {
    private BeanFactory beanFactory;
    private ResourceLoader resourceLoader;
    private ClassLoader classLoader;
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader=classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        if (!AutoConfigurationPackages.has(beanFactory)){
            log.warn("cannot determine autoconfigure packages,may cause cannot get interfaces with 'MyMapper'");
        }
        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        ClassPathMyMapperScanner classPathMyMapperScanner = new ClassPathMyMapperScanner(beanDefinitionRegistry, classLoader);
        if (resourceLoader!=null){
            classPathMyMapperScanner.setResourceLoader(resourceLoader);
        }
        String[] packageArr = packages.toArray(new String[0]);
        log.info("begin scan interfaces with annotation 'MyMapper'");
        classPathMyMapperScanner.registerFilters();
        classPathMyMapperScanner.doScan(packageArr);
    }
}
