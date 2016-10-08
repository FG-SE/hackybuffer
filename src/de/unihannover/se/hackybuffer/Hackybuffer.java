/**
    This file is part of Hackybuffer.

    Hackybuffer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Hackybuffer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Hackybuffer. If not, see <http://www.gnu.org/licenses/>.
 */

package de.unihannover.se.hackybuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Allows data to be stored in the Hackystat format in a directory structure on a disk drive.
 * In contrast to the normal Hackystat connectors, this does not need a special infrastructure running.
 * Additionally, the library is very lightweight.
 *
 * The data can later be imported from the directory into a running Hackystat server.
 */
public class Hackybuffer {

    private final File rootDir;
    private final Random rnd;

    private long lastTime;

    /**
     * Creates a new Hackybuffer instance.
     * @param rootDir The directory where the data will be stored. Has to exist.
     * @throws FileNotFoundException thrown when the directory does not exist.
     */
    public Hackybuffer(File rootDir) throws FileNotFoundException {
        if (!rootDir.exists()) {
            throw new FileNotFoundException("The root directory " + rootDir + " does not exist.");
        }
        this.rootDir = rootDir;
        this.rnd = new Random();
        this.lastTime = Long.MIN_VALUE;
    }

    /**
     * Writes a sensor event to the corresponding directory. The current time is used as the timestamp.
     * If the current time is not after the last time (in millisecond resolution), the timestamp is
     * artificially moved to the future to ensure that time and causality order are consistent.
     * @param tool The tool from which the event originates.
     * @param sensorDataType The data type of the sensor data. See the wiki for existing data types.
     * @param resource The resource (e.g. file or ticket) to which the event applies.
     * @param owner The owner of the event / user performing the event. Preferably an email address.
     * @param properties Further properties of the event.
     * @throws ParserConfigurationException
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public synchronized void writeData(
                    String tool,
                    String sensorDataType,
                    String resource,
                    String owner,
                    Map<String, String> properties)
                throws HackybufferException {

        try {
            final long time = System.currentTimeMillis();
            if (time <= this.lastTime) {
                this.lastTime++;
            } else {
                this.lastTime = time;
            }
            final XMLGregorianCalendar timestamp = this.toXml(this.lastTime);


            final DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            final DocumentBuilder b = f.newDocumentBuilder();
            final Document doc = b.newDocument();
            final Element sensorData = doc.createElement("SensorData");
            doc.appendChild(sensorData);

            sensorData.appendChild(this.createTextElement(doc, "Timestamp", timestamp.toXMLFormat()));
            sensorData.appendChild(this.createTextElement(doc, "Runtime", timestamp.toXMLFormat()));
            sensorData.appendChild(this.createTextElement(doc, "Tool", tool));
            sensorData.appendChild(this.createTextElement(doc, "SensorDataType", sensorDataType));
            sensorData.appendChild(this.createTextElement(doc, "Resource", resource));
            sensorData.appendChild(this.createTextElement(doc, "Owner", owner));

            final Element propertiesElement = doc.createElement("Properties");
            for (final Entry<String, String> e : properties.entrySet()) {
                final Element propertyElement = doc.createElement("Property");
                propertyElement.appendChild(this.createTextElement(doc, "Key", e.getKey()));
                propertyElement.appendChild(this.createTextElement(doc, "Value", e.getValue()));
                propertiesElement.appendChild(propertyElement);
            }
            sensorData.appendChild(propertiesElement);

            final File ownerDir = new File(this.rootDir, this.toValidFilename(owner));
            final File dayDir = new File(ownerDir,
                            String.format("%04d_%02d_%02d", timestamp.getYear(), timestamp.getMonth(), timestamp.getDay()));
            dayDir.mkdirs();
            final File file = new File(dayDir, this.toValidFilename(String.format("%s_%s", timestamp, tool)));

            File uniqueFile = file;
            while (uniqueFile.exists()) {
                uniqueFile = new File(file.toString() + "_" + this.rnd.nextInt(10000));
            }
            //there is some chance of a race condition remaining that we ignore for now

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (final TransformerException | ParserConfigurationException | DatatypeConfigurationException e) {
            throw new HackybufferException(e);
        }
    }

    private XMLGregorianCalendar toXml(long time) throws DatatypeConfigurationException {
        final GregorianCalendar g = new GregorianCalendar();
        g.setTimeInMillis(time);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(g);
    }

    private String toValidFilename(String s) {
        return s.replace('/', '-').replace('\\', '-').replace(':', '-').replace('\'', '-').replace('"', '-')
                        .replace('<', '-').replace('>', '-').replace('?', '-').replace('*', '-').replace('|', '-');
    }

    private Element createTextElement(Document doc, String name, String content) {
        final Element ret = doc.createElement(name);
        ret.setTextContent(content);
        return ret;
    }

}
