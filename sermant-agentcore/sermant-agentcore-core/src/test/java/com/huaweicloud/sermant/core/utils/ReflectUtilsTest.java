/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sermant.core.utils;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;

/**
 * 反射工具类测试
 *
 * @author zhouss
 * @since 2022-09-15
 */
public class ReflectUtilsTest {
    @Test
    public void invokeMethodWithNoneParameter() {
        final TestReflect testReflect = new TestReflect();
        String methodName = "noParams";
        final Optional<Object> result = ReflectUtils.invokeMethodWithNoneParameter(testReflect, methodName);
        Assert.assertTrue(result.isPresent() && result.get() instanceof String);
        Assert.assertEquals(result.get(), testReflect.noParams());
    }

    @Test
    public void invokeMethod() {
        final TestReflect testReflect = new TestReflect();
        String name = "Mike";
        final Optional<Object> hasParams = ReflectUtils
                .invokeMethod(testReflect, "hasParams", new Class[]{String.class}, new Object[]{name});
        Assert.assertTrue(hasParams.isPresent() && hasParams.get() instanceof String);
        Assert.assertEquals(hasParams.get(), testReflect.hasParams(name));
    }

    @Test
    public void testInvokeMethod() {
        int params = 88;
        final Optional<Object> staticMethod = ReflectUtils
                .invokeMethod(TestReflect.class.getName(), "staticMethod", new Class[]{int.class},
                        new Object[]{params});
        Assert.assertTrue(staticMethod.isPresent() && staticMethod.get() instanceof String);
        Assert.assertEquals(TestReflect.staticMethod(params), staticMethod.get());
    }

    @Test
    public void testInvokeMethod1() {
        int params = 88;
        final Optional<Object> staticMethod = ReflectUtils
                .invokeMethod(TestReflect.class, "staticMethod", new Class[]{int.class},
                        new Object[]{params});
        Assert.assertTrue(staticMethod.isPresent() && staticMethod.get() instanceof String);
        Assert.assertEquals(TestReflect.staticMethod(params), staticMethod.get());

        // 没有找到方法
        final Optional<Object> test = ReflectUtils.invokeMethod(TestReflect.class, "test", new Class[]{int.class},
                new Object[]{params});
        Assert.assertFalse(test.isPresent());

    }

    @Test
    public void testInvokeMethod2() throws NoSuchMethodException {
        final Method invokeMethod = TestReflect.class.getDeclaredMethod("invokeMethod");
        final TestReflect testReflect = new TestReflect();
        final Optional<Object> result = ReflectUtils.invokeMethod(testReflect, invokeMethod, null);
        Assert.assertTrue(result.isPresent() && result.get() instanceof Integer);
        Assert.assertEquals(result.get(), Integer.MAX_VALUE);
    }

    @Test
    public void findMethod() {
        final Optional<Method> findMethod = ReflectUtils.findMethod(TestReflect.class, "findMethod", null);
        Assert.assertTrue(findMethod.isPresent());
        final Optional<Method> test = ReflectUtils.findMethod(TestReflect.class, "test", null);
        Assert.assertFalse(test.isPresent());
        final Optional<Method> findMethodWithParam = ReflectUtils
                .findMethod(TestReflect.class, "findMethodWithParam", new Class[]{String.class, int.class});
        Assert.assertTrue(findMethodWithParam.isPresent());
        final Optional<Object> reflect = ReflectUtils.buildWithConstructor(ReflectUtils.class, null, null);
        Assert.assertTrue(reflect.isPresent());
        final Optional<Object> methodCache = ReflectUtils.getFieldValue(reflect.get(), "METHOD_CACHE");
        Assert.assertTrue(methodCache.isPresent() && methodCache.get() instanceof Map);
        Assert.assertFalse(((Map<?, ?>) methodCache.get()).isEmpty());
    }

    @Test
    public void buildWithConstructor() {
        final Optional<Object> result = ReflectUtils.buildWithConstructor(TestReflect.class.getName(), null, null);
        Assert.assertTrue(result.isPresent() && result.get() instanceof TestReflect);
        final Optional<Object> paramsResult = ReflectUtils.buildWithConstructor(TestReflect.class.getName(),
                new Class[] {int.class, int.class},new Object[] {1, 2});
        Assert.assertTrue(paramsResult.isPresent() && paramsResult.get() instanceof TestReflect);
        final TestReflect reflect = (TestReflect) paramsResult.get();
        Assert.assertEquals(reflect.x + reflect.y, 3);
    }

    @Test
    public void testBuildWithConstructor() {
        final Optional<Object> result = ReflectUtils.buildWithConstructor(TestReflect.class, null, null);
        Assert.assertTrue(result.isPresent() && result.get() instanceof TestReflect);
        final Optional<Object> paramsResult = ReflectUtils.buildWithConstructor(TestReflect.class,
                new Class[] {int.class, int.class},new Object[] {1, 2});
        Assert.assertTrue(paramsResult.isPresent() && paramsResult.get() instanceof TestReflect);
        final TestReflect reflect = (TestReflect) paramsResult.get();
        Assert.assertEquals(reflect.x + reflect.y, 3);
    }

    @Test
    public void findConstructor() {
        final Optional<Constructor<?>> constructor = ReflectUtils.findConstructor(TestReflect.class, null);
        Assert.assertTrue(constructor.isPresent());
        final Optional<Constructor<?>> paramsCons = ReflectUtils.findConstructor(TestReflect.class,
                new Class[] {int.class, int.class});
        Assert.assertTrue(paramsCons.isPresent());
        final Optional<Constructor<?>> noFoundCons = ReflectUtils.findConstructor(TestReflect.class,
                new Class[] {Integer.class, Integer.class});
        Assert.assertFalse(noFoundCons.isPresent());
    }

    @Test
    public void setFieldValue() throws NoSuchFieldException, IllegalAccessException {
        final TestReflect reflect = new TestReflect();
        int x = 102, y = 1899;
        ReflectUtils.setFieldValue(reflect, "x", x);
        ReflectUtils.setFieldValue(reflect, "y", y);
        Assert.assertEquals(reflect.x + reflect.y, x + y);
    }

    @Test
    public void updateFinalModifierField() throws NoSuchFieldException {
        final Field finalField = TestReflect.class.getDeclaredField("finalField");
        Assert.assertTrue(Modifier.isFinal(finalField.getModifiers()));
        ReflectUtils.updateFinalModifierField(finalField);
        Assert.assertFalse(Modifier.isFinal(finalField.getModifiers()));
    }

    @Test
    public void getFieldValue() {
        final TestReflect reflect = new TestReflect();
        final Optional<Object> finalField = ReflectUtils.getFieldValue(reflect, "finalField");
        Assert.assertTrue(finalField.isPresent() && finalField.get() instanceof String);
        Assert.assertEquals(finalField.get(), reflect.finalField);
        final Optional<Object> reflectUtils = ReflectUtils.buildWithConstructor(ReflectUtils.class, null, null);
        Assert.assertTrue(reflectUtils.isPresent());
        final Optional<Object> fieldCache = ReflectUtils.getFieldValue(reflectUtils.get(), "FIELD_CACHE");
        Assert.assertTrue(fieldCache.isPresent() && fieldCache.get() instanceof Map);
        Assert.assertFalse(((Map<?, ?>) fieldCache.get()).isEmpty());
    }

    static class TestReflect {
        private final String finalField = "test";
        int x;
        int y;

        TestReflect() {

        }

        TestReflect(int x, int y) {
            this.x = x;
            this.y = y;
        }
        private String noParams() {
            return "noParams";
        }

        private String hasParams(String name) {
            return "hello " + name;
        }

        private static String staticMethod(int params) {
            return "I am static method " + params;
        }

        int invokeMethod() {
            return Integer.MAX_VALUE;
        }

        void findMethod() {

        }

        void findMethodWithParam(String param, int is) {

        }
    }
}
