package org.dyndns.delphyne.rawr

import groovy.beans.Bindable
import groovy.swing.SwingBuilder
import java.awt.*
import javax.swing.*
import javax.swing.filechooser.*

class Interface {
	def swing = new SwingBuilder()
	
	def model = new FileNamesModel()
	
	def winprop = System.getenv("PROGRAMFILES")
	def luadirs = [new File("$winprop/World of Warcraft/"), new File("/Applications/")]
	
	public Interface() {
		def fileDialog = new FileDialog(null)
		
		swing.edt {
			frame(title:'Rawr Data Convertor', pack:true, show: true, defaultCloseOperation:JFrame.EXIT_ON_CLOSE) {
				vbox {
					hbox {
						button("RawrExport.lua", actionPerformed: {
							fileDialog.title = "Select your RawrExport.lua file"
							fileDialog.directory = luadirs.find { File file ->
								file.exists()
							}?.path
							fileDialog.file = model.savedVariables
							fileDialog.mode = FileDialog.LOAD
							fileDialog.filenameFilter = [accept: { dir,name -> name ==~ /RawrExport.lua/ }] as FilenameFilter
							fileDialog.show()
							model.savedVariables = new File(fileDialog.directory, fileDialog.file)
						})
						textField(text: bind(source: model, sourceProperty: "savedVariables"), columns: 60)
					}
					
					hbox {
						button("Character.xml", actionPerformed: {
							fileDialog.title = "Select your Rawr Character XML file"
							fileDialog.directory = null
							fileDialog.file = model.rawrXml
							fileDialog.mode = FileDialog.LOAD
							fileDialog.filenameFilter = [accept: { dir,name -> name ==~ /.*?\.xml/ }] as FilenameFilter
							fileDialog.show()
							model.rawrXml = new File(fileDialog.directory, fileDialog.file)
						})
						textField(text: bind(source: model, sourceProperty: "rawrXml"), columns: 60)
					}
					
					hglue()
					button("Go!", actionPerformed: {
						Main.execute(model.savedVariables, model.rawrXml)
					})
					hglue()
				}
			}
		}
	}
}

class FileNamesModel {
    @Bindable String savedVariables
    @Bindable String rawrXml
}