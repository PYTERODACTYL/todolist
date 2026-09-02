package br.com.pyteravila.todolist.utils;

public class Utils {
    
    public static void copyNonNullProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }
    
    public static String[] getNullPropertyNames(Object source) {
        final var emptyNames = new java.util.HashSet<String>();
        final var beanWrapper = new org.springframework.beans.BeanWrapperImpl(source);
        for (var propertyDescriptor : beanWrapper.getPropertyDescriptors()) {
            var propertyValue = beanWrapper.getPropertyValue(propertyDescriptor.getName());
            if (propertyValue == null) {
                emptyNames.add(propertyDescriptor.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
    
}
