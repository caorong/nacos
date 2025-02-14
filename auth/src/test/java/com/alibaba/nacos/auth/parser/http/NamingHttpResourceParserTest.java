/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.auth.parser.http;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.CommonParams;
import com.alibaba.nacos.plugin.auth.api.Resource;
import com.alibaba.nacos.common.utils.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class NamingHttpResourceParserTest {
    
    @Mock
    private HttpServletRequest request;
    
    private NamingHttpResourceParser resourceParser;
    
    @Before
    public void setUp() throws Exception {
        resourceParser = new NamingHttpResourceParser();
    }
    
    @Test
    public void testParseWithFullContext() {
        Mockito.when(request.getParameter(eq(CommonParams.NAMESPACE_ID))).thenReturn("testNs");
        Mockito.when(request.getParameter(eq(CommonParams.GROUP_NAME))).thenReturn("testG");
        Mockito.when(request.getParameter(eq(CommonParams.SERVICE_NAME))).thenReturn("testS");
        Resource actual = resourceParser.parse(request, Constants.Naming.NAMING_MODULE);
        assertEquals("testNs", actual.getNamespaceId());
        assertEquals("testG", actual.getGroup());
        assertEquals("testS", actual.getName());
        assertEquals(Constants.Naming.NAMING_MODULE, actual.getType());
    }
    
    @Test
    public void testParseWithoutNamespace() {
        Mockito.when(request.getParameter(eq(CommonParams.GROUP_NAME))).thenReturn("testG");
        Mockito.when(request.getParameter(eq(CommonParams.SERVICE_NAME))).thenReturn("testS");
        Resource actual = resourceParser.parse(request, Constants.Naming.NAMING_MODULE);
        assertEquals(StringUtils.EMPTY, actual.getNamespaceId());
        assertEquals("testG", actual.getGroup());
        assertEquals("testS", actual.getName());
        assertEquals(Constants.Naming.NAMING_MODULE, actual.getType());
    }
    
    @Test
    public void testParseWithoutGroup() {
        Mockito.when(request.getParameter(eq(CommonParams.NAMESPACE_ID))).thenReturn("testNs");
        Mockito.when(request.getParameter(eq(CommonParams.SERVICE_NAME))).thenReturn("testS");
        Resource actual = resourceParser.parse(request, Constants.Naming.NAMING_MODULE);
        assertEquals("testNs", actual.getNamespaceId());
        assertEquals(Constants.DEFAULT_GROUP, actual.getGroup());
        assertEquals("testS", actual.getName());
        assertEquals(Constants.Naming.NAMING_MODULE, actual.getType());
    }
    
    @Test
    public void testParseWithGroupInService() {
        Mockito.when(request.getParameter(eq(CommonParams.NAMESPACE_ID))).thenReturn("testNs");
        Mockito.when(request.getParameter(eq(CommonParams.SERVICE_NAME))).thenReturn("testG@@testS");
        Resource actual = resourceParser.parse(request, Constants.Naming.NAMING_MODULE);
        assertEquals("testNs", actual.getNamespaceId());
        assertEquals("testG", actual.getGroup());
        assertEquals("testS", actual.getName());
        assertEquals(Constants.Naming.NAMING_MODULE, actual.getType());
    }
    
    @Test
    public void testParseWithoutService() {
        Mockito.when(request.getParameter(eq(CommonParams.NAMESPACE_ID))).thenReturn("testNs");
        Mockito.when(request.getParameter(eq(CommonParams.GROUP_NAME))).thenReturn("testG");
        Resource actual = resourceParser.parse(request, Constants.Naming.NAMING_MODULE);
        assertEquals("testNs", actual.getNamespaceId());
        assertEquals("testG", actual.getGroup());
        assertEquals(com.alibaba.nacos.plugin.auth.constant.Constants.Resource.ANY, actual.getName());
        assertEquals(Constants.Naming.NAMING_MODULE, actual.getType());
    }
    
    @Test
    public void testParseWithoutGroupAndService() {
        Mockito.when(request.getParameter(eq(CommonParams.NAMESPACE_ID))).thenReturn("testNs");
        Resource actual = resourceParser.parse(request, Constants.Naming.NAMING_MODULE);
        assertEquals("testNs", actual.getNamespaceId());
        assertEquals(com.alibaba.nacos.plugin.auth.constant.Constants.Resource.ANY, actual.getGroup());
        assertEquals(com.alibaba.nacos.plugin.auth.constant.Constants.Resource.ANY, actual.getName());
        assertEquals(Constants.Naming.NAMING_MODULE, actual.getType());
    }
}
