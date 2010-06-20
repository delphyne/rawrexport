package org.dyndns.delphyne.rawr

import java.io.PrintWriter;

import groovy.util.XmlNodePrinter;

class RawrXml {
    def xmlFile
    def originalXml
    
    def merge(def data) {
        def knownItems = data.clone()
        
        originalXml = new XmlParser().parse(new File(xmlFile))

        originalXml.AvailableItems.each { node ->
            def item = node.text() as int
            knownItems.remove(item)
        }
        
        knownItems.each { item ->
            originalXml.appendNode("AvailableItems", item.key)
        }
        
        return toString()
    }
    
    String toString() {
        def writer = new StringWriter()
        def printer = new XmlNodePrinter(new PrintWriter(writer))
        printer.setPreserveWhitespace(true)
        printer.print(originalXml)
        return writer.toString()
    }
}
