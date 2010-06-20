package org.dyndns.delphyne.rawr

class SavedVariables {
	def varsFile
	def parse() {
		def text = new File(varsFile).text.replaceAll(~/\s|\[|\]/, "")
		text = text.substring(0,text.indexOf("=")+1) + text.substring(text.indexOf("=")+1).replaceAll(~/=/,":")
		text = text.replaceAll(~/\{/, "[")
		text = text.replaceAll(~/\}/, "]")
		
		def binding = new Binding()
		def shell = new GroovyShell(binding)
		shell.evaluate(text)
		return binding.getVariable("RawrExportAvailableItems")
	}
}
