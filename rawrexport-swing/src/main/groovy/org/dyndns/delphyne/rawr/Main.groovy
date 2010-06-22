package org.dyndns.delphyne.rawr

class Main {
    static String outFile
    
    static void main(String[] args) {
        if (args.size() == 2) {
            outFile = args[1]
            execute(new File(args[0]).text, new File(args[1]).text)
        } else if (args.size() > 0) {
            println("usage: ${Main.class.name} <RawrExport.lua> <Character.xml>")
        } else {
            new Interface()
        }
    }
    
    private static void execute(String savedVars, String rawrXml, String fileToWrite = outFile) {
        def dataToMerge = new SavedVariables(lua: savedVars).parse()
        def rawr = new RawrXml(xml: rawrXml)
        def outxml = rawr.merge(dataToMerge)
        new File(fileToWrite).write(outxml)
    }
}