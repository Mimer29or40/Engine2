package engine.gui.theme;

import com.google.gson.stream.JsonReader;
import engine.color.Color;
import engine.color.Colorc;
import engine.gui.util.Rect;
import engine.render.Font;
import engine.render.Texture;
import engine.util.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static engine.util.Util.getPath;
import static engine.util.Util.join;

public class Theme
{
    private static final Logger LOGGER = new Logger();
    
    private final HashMap<String, Color> baseColors = new HashMap<>();
    private final FontData               baseFontInfo;
    
    private String themeResourcePath;
    private long   themeResourceLastModified;
    
    private final HashMap<String, HashMap<String, Color>>   elementColors    = new HashMap<>();
    private final HashMap<String, HashMap<String, IData>>   elementImageData = new HashMap<>();
    private final HashMap<String, HashMap<String, Texture>> elementImages    = new HashMap<>();
    private final HashMap<String, FData>                    elementFontData  = new HashMap<>();
    private final HashMap<String, Font>                     elementFonts     = new HashMap<>();
    private final HashMap<String, HashMap<String, String>>  elementMiscData  = new HashMap<>();
    
    private final HashMap<String, Texture> loadedImages = new HashMap<>();
    
    public Theme()
    {
        this.baseColors.put("normal_bg", new Color("#25292e"));
        this.baseColors.put("hovered_bg", new Color("#35393e"));
        this.baseColors.put("disabled_bg", new Color("#25292e"));
        this.baseColors.put("selected_bg", new Color("#193754"));
        this.baseColors.put("active_bg", new Color("#193754"));
        this.baseColors.put("dark_bg", new Color("#15191e"));
        this.baseColors.put("normal_text", new Color("#c5cbd8"));
        this.baseColors.put("hovered_text", new Color("#FFFFFF"));
        this.baseColors.put("selected_text", new Color("#FFFFFF"));
        this.baseColors.put("active_text", new Color("#FFFFFF"));
        this.baseColors.put("disabled_text", new Color("#6d736f"));
        this.baseColors.put("normal_border", new Color("#DDDDDD"));
        this.baseColors.put("hovered_border", new Color("#EDEDED"));
        this.baseColors.put("disabled_border", new Color("#909090"));
        this.baseColors.put("selected_border", new Color("#294764"));
        this.baseColors.put("active_border", new Color("#294764"));
        this.baseColors.put("link_text", new Color("#c5cbFF"));
        this.baseColors.put("link_hover", new Color("#a5abDF"));
        this.baseColors.put("link_selected", new Color("#DFabDF"));
        this.baseColors.put("text_shadow", new Color("#777777"));
        this.baseColors.put("filled_bar", new Color("#f4251b"));
        this.baseColors.put("unfilled_bar", new Color("#CCCCCC"));
        
        FData baseFontInfo = new FData();
        baseFontInfo.name           = "fira_code";
        baseFontInfo.size           = 14;
        baseFontInfo.bold           = false;
        baseFontInfo.italic         = false;
        baseFontInfo.regularPath    = "fonts/FiraCode-Regular.ttf";
        baseFontInfo.boldPath       = "fonts/FiraCode-Bold.ttf";
        baseFontInfo.italicPath     = "fonts/FiraMono-RegularItalic.ttf";
        baseFontInfo.boldItalicPath = "fonts/FiraMono-BoldItalic.ttf";
        
        this.baseFontInfo = new FontData(baseFontInfo);
        
        loadTheme("themes/default.json");
    }
    
    public void loadTheme(String filePath)
    {
        Theme.LOGGER.fine("Loading Theme: " + filePath);
        
        Path path = getPath(this.themeResourcePath = filePath);
        try
        {
            this.themeResourceLastModified = Files.getLastModifiedTime(path).toMillis();
        }
        catch (IOException ignored)
        {
            this.themeResourceLastModified = 0;
        }
        
        try (FileReader fileReader = new FileReader(path.toString()); JsonReader jsonReader = new JsonReader(fileReader))
        {
            jsonReader.beginObject();
            while (jsonReader.hasNext())
            {
                String element = jsonReader.nextName();
                
                if (element.equals("defaults"))
                {
                    loadDefaultColors(jsonReader);
                }
                else
                {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext())
                    {
                        String dataTypeName = jsonReader.nextName();
                        
                        switch (dataTypeName)
                        {
                            case "prototype":
                                loadPrototype(dataTypeName, jsonReader.nextString());
                                break;
                            case "font":
                                loadElementFont(jsonReader, element);
                                break;
                            default:
                                jsonReader.beginObject();
                                while (jsonReader.hasNext())
                                {
                                    String state = jsonReader.nextName();
                                    
                                    switch (dataTypeName)
                                    {
                                        case "colors":
                                        case "colours":
                                            loadElementColor(element, state, jsonReader.nextString());
                                            break;
                                        case "images":
                                            loadElementImage(jsonReader, element, state);
                                            break;
                                        case "misc":
                                            loadElementMisc(element, state, jsonReader.nextString());
                                            break;
                                    }
                                }
                                jsonReader.endObject();
                                break;
                        }
                    }
                    jsonReader.endObject();
                }
            }
            jsonReader.endObject();
            
            loadImages();
            loadFonts();
        }
        catch (IOException e)
        {
            Theme.LOGGER.warning("Failed to load Theme: " + filePath);
        }
    }
    
    public void reload()
    {
        if (this.themeResourcePath == null)
        {
            Theme.LOGGER.warning("Could not reload theme before it was loaded!");
            return;
        }
        loadTheme(this.themeResourcePath);
    }
    
    public boolean shouldReload()
    {
        if (this.themeResourcePath != null)
        {
            try
            {
                long lastModified = Files.getLastModifiedTime(getPath(this.themeResourcePath)).toMillis();
                if (lastModified != this.themeResourceLastModified)
                {
                    this.themeResourceLastModified = lastModified;
                    reload();
                }
            }
            catch (IOException ignored) { }
        }
        return false;
    }
    
    private void loadDefaultColors(JsonReader jsonReader) throws IOException
    {
        Theme.LOGGER.finer("Loading Default Colors");
        
        jsonReader.beginObject();
        while (jsonReader.hasNext())
        {
            String dataTypeName = jsonReader.nextName();
            
            if (dataTypeName.equals("colors") || dataTypeName.equals("colours"))
            {
                jsonReader.beginObject();
                while (jsonReader.hasNext())
                {
                    String element = jsonReader.nextName();
                    Color  color   = new Color(jsonReader.nextString());
                    
                    Theme.LOGGER.finest("Found color (%s) for element (%s)", element, color);
                    
                    this.baseColors.put(element, color);
                }
                jsonReader.endObject();
            }
        }
        jsonReader.endObject();
    }
    
    private void loadPrototype(String element, String prototype)
    {
        Theme.LOGGER.finer("Loading prototype (%s) for element (%s)", prototype, element);
        
        ArrayList<String> foundPrototypes = new ArrayList<>();
        
        if (this.elementColors.containsKey(prototype))
        {
            Theme.LOGGER.finest("Adding colors from prototype (%s) to element (%s)", prototype, element);
            
            if (!this.elementColors.containsKey(element)) this.elementColors.put(element, new HashMap<>());
            this.elementColors.get(element).putAll(this.elementColors.get(prototype));
            foundPrototypes.addAll(this.elementColors.get(prototype).keySet());
        }
        
        if (this.elementImages.containsKey(prototype))
        {
            Theme.LOGGER.finest("Adding images from prototype (%s) to element (%s)", prototype, element);
            
            if (!this.elementImages.containsKey(element)) this.elementImages.put(element, new HashMap<>());
            this.elementImages.get(element).putAll(this.elementImages.get(prototype));
            foundPrototypes.addAll(this.elementImages.get(prototype).keySet());
        }
        
        if (this.elementFontData.containsKey(prototype))
        {
            Theme.LOGGER.finest("Adding font from prototype (%s) to element (%s)", prototype, element);
            
            this.elementFontData.put(element, this.elementFontData.get(prototype));
            foundPrototypes.add(element);
        }
        
        if (this.elementMiscData.containsKey(prototype))
        {
            Theme.LOGGER.finest("Adding misc data from prototype (%s) to element (%s)", prototype, element);
            
            if (!this.elementMiscData.containsKey(element)) this.elementMiscData.put(element, new HashMap<>());
            this.elementMiscData.get(element).putAll(this.elementMiscData.get(prototype));
            foundPrototypes.addAll(this.elementMiscData.get(prototype).keySet());
        }
        
        if (foundPrototypes.isEmpty()) Theme.LOGGER.warning("Failed to find any prototype data with ID: " + prototype);
    }
    
    private void loadElementFont(JsonReader jsonReader, String element) throws IOException
    {
        Theme.LOGGER.finer("Loading font data for element (%s)", element);
        
        if (!this.elementFontData.containsKey(element)) this.elementFontData.put(element, new FData());
        
        FData fData = this.elementFontData.get(element);
        
        jsonReader.beginObject();
        while (jsonReader.hasNext())
        {
            String name = jsonReader.nextName();
            String data = jsonReader.nextString();
            switch (name)
            {
                case "name":
                    fData.name = data;
                    break;
                case "size":
                    fData.size = Integer.parseInt(data);
                    break;
                case "bold":
                    fData.bold = Integer.parseInt(data) == 1;
                    break;
                case "italic":
                    fData.italic = Integer.parseInt(data) == 1;
                    break;
                case "regular_path":
                    fData.regularPath = data;
                    break;
                case "bold_path":
                    fData.boldPath = data;
                    break;
                case "italic_path":
                    fData.italicPath = data;
                    break;
                case "bold_italic_path":
                    fData.boldItalicPath = data;
                    break;
            }
        }
        jsonReader.endObject();
    }
    
    private void loadElementColor(String element, String colorKey, String colorString)
    {
        Theme.LOGGER.finer("Loading color (%s) with value, (%s) for element (%s)", colorKey, colorString, element);
        
        if (!this.elementColors.containsKey(element)) this.elementColors.put(element, new HashMap<>());
        this.elementColors.get(element).put(colorKey, new Color(colorString));
    }
    
    private void loadElementImage(JsonReader jsonReader, String element, String imageKey) throws IOException
    {
        Theme.LOGGER.finer("Loading image data (%s) for element (%s)", imageKey, element);
        
        String path       = null;
        String rectString = null;
        
        jsonReader.beginObject();
        while (jsonReader.hasNext())
        {
            String name = jsonReader.nextName();
            String data = jsonReader.nextString();
            
            switch (name)
            {
                case "path":
                    path = data;
                    break;
                case "sub_surface_rect":
                    rectString = data;
                    break;
            }
        }
        jsonReader.endObject();
        
        if (!this.elementImageData.containsKey(element)) this.elementImageData.put(element, new HashMap<>());
        if (!this.elementImageData.get(element).containsKey(imageKey))
        {
            this.elementImageData.get(element).put(imageKey, new IData());
        }
        else
        {
            this.elementImageData.get(element).get(imageKey).changed = false;
        }
        String imagePath = path;
        if (!this.elementImageData.get(element).get(imageKey).path.equals(imagePath)) this.elementImageData.get(element).get(imageKey).changed = true;
        this.elementImageData.get(element).get(imageKey).path = imagePath;
        if (rectString != null)
        {
            String[] rectList = rectString.strip().split(",");
            if (rectList.length == 4)
            {
                Rect rect = new Rect(Integer.parseInt(rectList[0].strip()),
                                     Integer.parseInt(rectList[1].strip()),
                                     Integer.parseInt(rectList[2].strip()),
                                     Integer.parseInt(rectList[3].strip()));
                if (!this.elementImageData.get(element).get(imageKey).rect.equals(rect)) this.elementImageData.get(element).get(imageKey).changed = true;
                this.elementImageData.get(element).get(imageKey).rect = rect;
            }
        }
    }
    
    private void loadElementMisc(String element, String miscKey, String miscString)
    {
        Theme.LOGGER.finer("Loading misc data (%s) with value (%s) for element (%s)", miscKey, miscString, element);
    
        if (!this.elementMiscData.containsKey(element)) this.elementMiscData.put(element, new HashMap<>());
        this.elementMiscData.get(element).put(miscKey, miscString);
    }
    
    private void loadImages()
    {
        Theme.LOGGER.finer("Loading found images");
        
        for (String elementKey : this.elementImageData.keySet())
        {
            HashMap<String, IData> dataMap = this.elementImageData.get(elementKey);
            if (!this.elementImages.containsKey(elementKey)) this.elementImages.put(elementKey, new HashMap<>());
            for (String imageKey : dataMap.keySet())
            {
                IData data = dataMap.get(imageKey);
                if (data.changed)
                {
                    Texture image;
                    if (this.loadedImages.containsKey(data.path))
                    {
                        Theme.LOGGER.finest("Image already loaded: " + data.path);
    
                        image = this.loadedImages.get(data.path);
                    }
                    else
                    {
                        Theme.LOGGER.finest("Loading image: " + data.path);
    
                        image = Texture.loadImage(data.path);
                        if (image.width() == 0) image = null;
                        if (image != null) this.loadedImages.put(data.path, image);
                    }
                    if (image != null)
                    {
                        if (data.rect != null)
                        {
                            Theme.LOGGER.finest("Creating sub texture: " + data.rect);
    
                            image = image.subTexture(data.rect.x(), data.rect.y(), data.rect.width(), data.rect.height());
                        }
                        this.elementImages.get(elementKey).put(imageKey, image);
                    }
                }
            }
        }
    }
    
    private void loadFonts()
    {
        Font.registerFont(this.baseFontInfo.name, this.baseFontInfo.regularPath, this.baseFontInfo.boldPath, this.baseFontInfo.italicPath, this.baseFontInfo.boldItalicPath);
        
        Font.preloadFont(this.baseFontInfo.name, this.baseFontInfo.size, this.baseFontInfo.bold, this.baseFontInfo.italic);
        
        for (String elementKey : this.elementFontData.keySet())
        {
            FData fontData = this.elementFontData.get(elementKey);
            
            if (fontData.regularPath != null)
            {
                Font.registerFont(fontData.name, fontData.regularPath, fontData.boldPath, fontData.italicPath, fontData.boldItalicPath);
            }
            
            this.elementFonts.put(elementKey, Font.getFont(fontData.name, fontData.size, fontData.bold, fontData.italic));
        }
    }
    
    public Colorc getColor(String[] objectIDs, String[] elementIDs, String colorID)
    {
        for (String combinedElementID : buildAllCombinedIDs(objectIDs, elementIDs))
        {
            if (this.elementColors.containsKey(combinedElementID) && this.elementColors.get(combinedElementID).containsKey(colorID))
            {
                return this.elementColors.get(combinedElementID).get(colorID);
            }
        }
        
        if (objectIDs != null)
        {
            for (String objectID : objectIDs)
            {
                if (objectID != null && this.elementColors.containsKey(objectID) && this.elementColors.get(objectID).containsKey(colorID))
                {
                    return this.elementColors.get(objectID).get(colorID);
                }
            }
        }
        
        if (elementIDs != null)
        {
            for (String elementID : elementIDs)
            {
                if (elementID != null && this.elementColors.containsKey(elementID) && this.elementColors.get(elementID).containsKey(colorID))
                {
                    return this.elementColors.get(elementID).get(colorID);
                }
            }
        }
        
        if (this.baseColors.containsKey(colorID)) return this.baseColors.get(colorID);
        
        ArrayList<String> colorParts = (ArrayList<String>) Arrays.asList(colorID.split("_"));
        
        int   bestFitKeyCount = 0;
        Color bestFitColor    = this.baseColors.get("normal_bg");
        for (String key : this.baseColors.keySet())
        {
            int count = 0;
            for (String keyWord : key.split("_")) if (colorParts.contains(keyWord)) count++;
            if (count > bestFitKeyCount)
            {
                bestFitKeyCount = count;
                bestFitColor    = this.baseColors.get(key);
            }
        }
        return bestFitColor;
    }
    
    public Texture getImage(String[] objectIDs, String[] elementIDs, String imageID)
    {
        for (String combinedElementID : buildAllCombinedIDs(objectIDs, elementIDs))
        {
            if (this.elementImages.containsKey(combinedElementID) && this.elementImages.get(combinedElementID).containsKey(imageID))
            {
                return this.elementImages.get(combinedElementID).get(imageID);
            }
        }
        return null;
    }
    
    public FontData getFontData(String[] objectIDs, String[] elementIDs)
    {
        for (String combinedElementID : buildAllCombinedIDs(objectIDs, elementIDs))
        {
            if (this.elementFontData.containsKey(combinedElementID))
            {
                return new FontData(this.elementFontData.get(combinedElementID));
            }
        }
        return this.baseFontInfo;
    }
    
    public Font getFont(String[] objectIDs, String[] elementIDs)
    {
        for (String combinedElementID : buildAllCombinedIDs(objectIDs, elementIDs))
        {
            if (this.elementFonts.containsKey(combinedElementID))
            {
                return this.elementFonts.get(combinedElementID);
            }
        }
        // return this.fontDictionary.findFont(this.baseFontInfo.name, this.baseFontInfo.size, this.baseFontInfo.bold, this.baseFontInfo.italic);
        return null;
    }
    
    public String getMiscData(String[] objectIDs, String[] elementIDs, String miscDataID)
    {
        for (String combinedElementID : buildAllCombinedIDs(objectIDs, elementIDs))
        {
            if (this.elementMiscData.containsKey(combinedElementID) && this.elementMiscData.get(combinedElementID).containsKey(miscDataID))
            {
                return this.elementMiscData.get(combinedElementID).get(miscDataID);
            }
        }
        return null;
    }
    
    public ArrayList<String> buildAllCombinedIDs(String[] objectIDs, String[] elementIDs)
    {
        ArrayList<String> combinedIDs = new ArrayList<>();
        
        if (objectIDs == null || elementIDs == null) return combinedIDs;
        
        if (objectIDs.length != elementIDs.length)
        {
            throw new RuntimeException("Object ID hierarchy is not equal in length to Element ID hierarchy." +
                                       "\nObject IDs: " + Arrays.toString(objectIDs) +
                                       "\nElement IDs: " + Arrays.toString(elementIDs) +
                                       "\n");
        }
        
        if (objectIDs.length != 0) getNextIDNode(null, objectIDs, elementIDs, 0, objectIDs.length, combinedIDs);
        
        ArrayList<String> currentIDs = new ArrayList<>(combinedIDs);
        while (!currentIDs.isEmpty())
        {
            for (int i = 0, n = currentIDs.size(); i < n; i++)
            {
                String currentID     = currentIDs.get(i);
                int    fullStopIndex = currentID.indexOf('.');
                if (fullStopIndex < 0) return combinedIDs;
                currentIDs.set(i, currentID.substring(fullStopIndex + 1));
                combinedIDs.add(currentIDs.get(i));
            }
        }
        
        return combinedIDs;
    }
    
    private static void getNextIDNode(Node currentNode, String[] objectIDs, String[] elementIDs, int index, int tree_size, ArrayList<String> combinedIDs)
    {
        if (index < tree_size)
        {
            if (objectIDs != null && index < objectIDs.length)
            {
                String objectID = objectIDs[index];
                if (objectID != null) getNextIDNode(new Node(objectID, currentNode), objectIDs, elementIDs, index + 1, tree_size, combinedIDs);
                getNextIDNode(new Node(elementIDs[index], currentNode), objectIDs, elementIDs, index + 1, tree_size, combinedIDs);
            }
        }
        else // Unwind
        {
            ArrayList<String> gatheredIDs = new ArrayList<>();
            Node              unwindNode  = currentNode;
            while (unwindNode != null)
            {
                gatheredIDs.add(unwindNode.id);
                unwindNode = unwindNode.parent;
            }
            Collections.reverse(gatheredIDs);
            combinedIDs.add(join(gatheredIDs, ".", "", ""));
        }
    }
    
    private static final class FData
    {
        private String name = Font.DEFAULT_FONT_FAMILY;
        private int    size = Font.DEFAULT_FONT_SIZE;
        
        private boolean bold   = false;
        private boolean italic = false;
        
        private String regularPath;
        private String boldPath       = null;
        private String italicPath     = null;
        private String boldItalicPath = null;
    }
    
    public static final class FontData
    {
        public final String  name;
        public final int     size;
        public final boolean bold;
        public final boolean italic;
        public final String  regularPath;
        public final String  boldPath;
        public final String  italicPath;
        public final String  boldItalicPath;
        
        private FontData(FData data)
        {
            this.name           = data.name;
            this.size           = data.size;
            this.bold           = data.bold;
            this.italic         = data.italic;
            this.regularPath    = data.regularPath;
            this.boldPath       = data.boldPath;
            this.italicPath     = data.italicPath;
            this.boldItalicPath = data.boldItalicPath;
        }
    }
    
    private static final class IData
    {
        private boolean changed = true;
        private String  path    = "";
        private Rect    rect;
    }
    
    private static final class Node
    {
        private final String id;
        private final Node   parent;
        
        public Node(String id, Node parent)
        {
            this.id     = id;
            this.parent = parent;
        }
    }
}
