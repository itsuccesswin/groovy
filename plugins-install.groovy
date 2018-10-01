import jenkins.model.Jenkins;

pm = Jenkins.instance.pluginManager

uc = Jenkins.instance.updateCenter
pm.plugins.each { plugin ->
plugin.disable()
}

deployed = false
def activatePlugin(plugin) {
if (! plugin.isEnabled()) {
  plugin.enable()
  deployed = true
}

plugin.getDependencies().each {
  activatePlugin(pm.getPlugin(it.shortName))
}
}

["git", "workflow-aggregator", "github-oauth", "job-dsl", "extended-read-permission"].each {
if (! pm.getPlugin(it)) {
  deployment = uc.getPlugin(it).deploy(true)
  deployment.get()
}
activatePlugin(pm.getPlugin(it))
}

if (deployed) {
Jenkins.instance.restart()
}
