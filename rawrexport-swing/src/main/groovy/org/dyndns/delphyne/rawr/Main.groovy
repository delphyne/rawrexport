package org.dyndns.delphyne.rawr

class Main {
    static void main(String[] args) {
        if (args.size() == 2) {
            execute(args[0], args[1])            
        } else if (args.size() > 0) {
            println("usage: ${Main.class.name} <RawrExport.lua> <Character.xml>")
        } else {
            new Interface()
        }
    }
    
    static void execute(def savedVars, def rawrXml) {
        def dataToMerge = new SavedVariables(varsFile:savedVars).parse()
        def rawr = new RawrXml(xmlFile:rawrXml)
        def outxml = rawr.merge(dataToMerge)
        new File(rawrXml).write(outxml)
    }
}