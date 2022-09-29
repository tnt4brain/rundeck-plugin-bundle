import com.dtolabs.rundeck.core.execution.ExecutionContext
import com.dtolabs.rundeck.core.logging.*
import com.dtolabs.rundeck.plugins.logging.LogFilterPlugin
import groovy.util.*

def convert(node){
    if (node instanceof String){ 
        return // ignore strings...
    }
    y = node.name().toString()
    def map = [name:(y)]
    if (!node.children().isEmpty() && !(node.children().get(0) instanceof String)) { 
        def nodearray = node.children().collectEntries{convert(it)}.findAll{it != null}
	map << [children: nodearray]
        } else if (node.text() != ''){
	map[(y)] = "${node.text()}"
        }
    if (!node.attributes().isEmpty()) {
        map <<  [attributes: node.attributes().collectEntries{it}]
        }
    map
    }

def toJsonBuilder = { String xml ->
    def pojo = convert(new XmlParser().parseText(xml))
    new groovy.json.JsonBuilder(pojo)
}

rundeckPlugin(LogFilterPlugin){
    title = "xml2json"
    description = "This plugin converts XML into JSON. No configuration options supported at the moment. For licensing information please see accompanying LICENSE file."
    version = "0.0.1"
    url = "https://github.com/tnt4brain/rundeck-xml2json-plugin"
    author = "© 2019, Sergey Pechenko"
    date = "2019-01-10T20:56:56Z"

    configuration {
	//no configuration supported at the moment
    }

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

