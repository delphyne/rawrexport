package org.dyndns.delphyne.rawr

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.commons.fileupload.FileItemIterator 
import org.apache.commons.fileupload.FileItemStream 
import org.apache.commons.fileupload.servlet.ServletFileUpload 
import org.apache.commons.fileupload.util.Streams 

class MergeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Boolean isSafe = ServletFileUpload.isMultipartContent(request)
        
        ServletFileUpload upload = new ServletFileUpload()
        
        FileItemIterator iter = upload.getItemIterator(request)
        
        Map<String,List> files = [:]
        
        while (iter.hasNext()) {
            FileItemStream stream = iter.next()
            if (!stream.isFormField()) {
                files[stream.fieldName] = new FileModel(name: stream.name, contents: Streams.asString(stream.openStream()))
            }
        }

        Map<String, Boolean> vars = new SavedVariables(lua: files.lua.contents).parse()
        String outXml = new RawrXml(xml: files.xml.contents).merge(vars)
                
        response.setHeader("Content-disposition", "attachment; filename=" + files.xml.name);
        response.setContentType("text/xml");
        
        response.writer.print(outXml)
    }
}
