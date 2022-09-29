import com.dtolabs.rundeck.plugins.option.OptionValuesPlugin
import groovy.json.JsonSlurper

rundeckPlugin(OptionValuesPlugin) {
    title="Gitlab branches Plugin"
    description="Plugin lets you populate list box with branch names"

    configuration {
        gitlab_token title: "Token", description: "This is the token to access Gitlab REST API"
        gitlab_host title: "Host", description: "This is the Gitlab endpoint"
	schema title: "Schema", description: "http or https, please"
        id title: "Project ID", description: "Gitlab project ID"
    }
    getOptionValues { config ->
        def options = []

	def z = new URL("${schema}://${gitlab-host}/projects/${id}/repository/branches").openConnection();
	z.setRequestProperty("PRIVATE-TOKEN", "${gitlab_token}")
	def getRC = z.getResponseCode();
	if(getRC.equals(200)) {
	    def jsonSlurper = new JsonSlurper();
	    def o = jsonSlurper.parseText(z.getInputStream().getText());
            def arr = o.values
	    for(def i : arr) {
		options.add([name:i.displayId,value:i.id])
	    }
	} else {
	  options.add([name:"Result",value:"Messed up, sorry"])
	}
        return options
    }
}
