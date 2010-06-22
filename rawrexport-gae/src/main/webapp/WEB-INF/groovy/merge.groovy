import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.FileItemIterator
import org.apache.commons.fileupload.FileItemStream
import org.apache.commons.fileupload.util.Streams

Boolean isSafe = ServletFileUpload.isMultipartContent(request)

ServletFileUpload upload = new ServletFileUpload()

FileItemIterator iter = upload.getItemIterator(request)

String lua
String xml
while (iter.hasNext()) {
    FileItemStream stream = iter.next()
    if (!stream.isFormField()) {
        def text = Streams.asString(stream.openStream())
        
        if (stream.fieldName == "lua") {
            lua = text
        } else if (stream.fieldName == "xml") {
            xml = text
        }
    }
}

html.html {
    head { title("output!") }
    body {
        h("lua")
        p(lua)
        h("xml")
        p(xml)
    }
}