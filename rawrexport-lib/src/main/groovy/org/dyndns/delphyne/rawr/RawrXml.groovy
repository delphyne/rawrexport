package org.dyndns.delphyne.rawr

import java.io.PrintWriter;

import groovy.util.XmlNodePrinter;

class RawrXml {
    String xml
    def originalXml
    
    def merge(def data) {
        def knownItems = data.clone()
        
        originalXml = new XmlParser().parseText(xml)

        originalXml.AvailableItems.each { node ->
            def item = node.text() as int
            knownItems.remove(item)
        }
        
        knownItems.each { k,v ->
            originalXml.appendNode("AvailableItems", k)
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
