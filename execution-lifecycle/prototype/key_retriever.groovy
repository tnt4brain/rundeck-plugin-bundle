import com.dtolabs.rundeck.core.dispatcher.ContextView
import com.dtolabs.rundeck.core.execution.ExecutionContext
import com.dtolabs.rundeck.core.dispatcher.DataContextUtils
import com.dtolabs.rundeck.core.execution.workflow.steps.StepException
import com.dtolabs.rundeck.core.logging.*
import com.dtolabs.rundeck.plugins.logging.LogFilterPlugin
import com.dtolabs.rundeck.core.plugins.Plugin
import com.dtolabs.rundeck.core.plugins.configuration.StringRenderingConstants
import com.dtolabs.rundeck.plugins.ServiceNameConstants
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty
import com.dtolabs.rundeck.plugins.descriptions.RenderingOption
import com.dtolabs.rundeck.plugins.descriptions.RenderingOptions
import com.dtolabs.rundeck.plugins.descriptions.SelectValues
import com.dtolabs.rundeck.plugins.step.PluginStepContext
import com.dtolabs.rundeck.plugins.step.StepPlugin
import com.google.common.base.Charsets
import com.google.common.io.CharStreams
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import groovy.util.*


rundeckPlugin(LogFilterPlugin){
    title = "keyretriever"
    description = "This plugin retrieves certain key/token from KeyStorage. No configuration options supported at the moment. For licensing information please see accompanying LICENSE file."
    version = "0.0.1"
    url = "https://bantu.ru"
    author = "© 2022, Sergey Pechenko"
    date = "2022-03-31T21:25:56Z"

    configuration {
    }

    // handleEvent  PluginLoggingContext context, LogEventControl event ->
    // complete  PluginLoggingContext context ->

    handleEvent { PluginLoggingContext context, LogEventControl event, Map configuration ->
	if (event.getEventType() == 'log' && event.loglevel == LogLevel.NORMAL) {
	    if (event.message.startsWith("<?xml")) { 
		event.message = "${toJsonBuilder(event.message).toPrettyString()}"
		event.addMetadata('content-data-type','application/json')
		event.addMetadata('content-meta:css-class', 'table-striped')
		event.addMetadata('content-meta:no-strip', 'true')
    	    }
	}
    }
}















/*
 * Copyright 2018 Rundeck, Inc. (http://rundeck.com)
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
package com.rundeck.plugins


@Plugin(service = ServiceNameConstants.WorkflowStep, name = LoadKeyStorageWorkflowStep.PROVIDER_NAME)
@PluginDescription(title = "Load key storage value", description = "Load the key storage value into a new variable (this variable will be exposed as clear not as secret)")
class LoadKeyStorageWorkflowStep implements StepPlugin {
    public static final String PROVIDER_NAME = 'load-keystorage'

    final String EMPTY = ''

    @PluginProperty(
            title = "Path",
            description = "Path",
            defaultValue = "",
            required = true
    )
    String path

    @PluginProperty(
            title = "Group",
            description = "New variable group.",
            defaultValue = "",
            required = true
    )
    String group

    @PluginProperty(
            title = "Name",
            description = "New variable name.",
            defaultValue = "",
            required = true
    )
    String variable


    @Override
    void executeStep(PluginStepContext context, Map<String, Object> configuration) throws StepException {
        def pass = getPrivateKeyStorageData(path, context.getExecutionContext())
        context.getExecutionContext().getOutputContext().addOutput(ContextView.global(), group, variable, pass)
    }


    String getPrivateKeyStorageData(String path, ExecutionContext context) {
        context.executionLogger.log(4, "$PROVIDER_NAME: Searching key path '$path'")
        if (null == path || path.isEmpty()) {
            context.executionLogger.log(4, "$PROVIDER_NAME: Empty key path")
            return EMPTY;
        }
        try {
            InputStream is = context
                    .getStorageTree()
                    .getResource(path)
                    .getContents()
                    .getInputStream();
            String result = CharStreams.toString(new InputStreamReader(
                    is, Charsets.UTF_8));
            return result;
        } catch (IOException e) {
            if (context.loglevel == 4) { //DEBUG
                e.printStackTrace();
            }
            context.executionLogger.log(0, e.message)
        }
        return EMPTY;
    }

}
