package br.com.stefanycampanhoni.todolist.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class ProjectUtils {

    public static void getNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertiesNames(source));
    }
    
    public static String[] getNullPropertiesNames(Object object) {
        final BeanWrapper obj = new BeanWrapperImpl(object);

        PropertyDescriptor[] pds = obj.getPropertyDescriptors();

        Set<String> emptyProps = new HashSet<>();

        for(PropertyDescriptor pd: pds) {
            if (obj.getPropertyValue(pd.getName()) == null) {
                emptyProps.add(pd.getName());
            }
        }

        return emptyProps.toArray(new String[emptyProps.size()]);
    }
}
