package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.util.Encode;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathSegmentImpl implements PathSegment
{
   private String path;
   private String original;
   private MultivaluedMap<String, String> matrixParameters = new MultivaluedMapImpl<String, String>();

   /**
    * @param segment encoded path segment
    * @param decode whether or not to decode values
    */
   public PathSegmentImpl(String segment, boolean decode)
   {
      this.original = segment;
      this.path = segment;
      int semicolon = segment.indexOf(';');
      if (semicolon >= 0)
      {
         if (semicolon > 0) this.path = segment.substring(0, semicolon);
         else this.path = "";
         String matrixParams = segment.substring(semicolon + 1);
         String[] params = matrixParams.split(";");
         for (String param : params)
         {
            String[] namevalue = param.split("=");
            if (namevalue != null && namevalue.length > 0)
            {
               String name = namevalue[0];
               if (decode) name = Encode.decodePath(name);
               String value = "";
               if (namevalue.length > 1)
               {
                  value = namevalue[1];
               }
               if (decode) value = Encode.decodePath(value);
               matrixParameters.add(name, value);
            }
         }
      }
      if (decode) this.path = Encode.decodePath(this.path);
   }

   public String getOriginal()
   {
      return original;
   }

   public String getPath()
   {
      return path;
   }

   public MultivaluedMap<String, String> getMatrixParameters()
   {
      return matrixParameters;
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer();
      if (path != null) buf.append(path);
      if (matrixParameters != null)
      {
         for (String name : matrixParameters.keySet())
         {
            for (String value : matrixParameters.get(name))
            {
               buf.append(";").append(name).append("=").append(value);
            }
         }
      }
      return buf.toString();
   }

   /**
    *
    * @param path encoded full path
    * @param decode whether or not to decode each segment
    * @return
    */
   public static List<PathSegment> parseSegments(String path, boolean decode)
   {
      List<PathSegment> pathSegments = new ArrayList<PathSegment>();
      int start = 0;
      if (path.startsWith("/")) start++;
      int length = path.length();
      do
      {
         String p;
         int slash = path.indexOf('/', start);
         if (slash < 0)
         {
            p = path.substring(start);
            start = length;
         }
         else
         {
            p = path.substring(start, slash);
            start = slash + 1;
         }
         pathSegments.add(new PathSegmentImpl(p, decode));
      } while (start < length);
      return pathSegments;
   }

}
