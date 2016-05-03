# Hackybuffer

A lightweight library to store software telemetry data in Hackystat format in a directory.

Software telemetry is the collection and analysis of data from various software engineering tools, to allow assessing and improving
the software development process. The Hackystat project (http://csdl.ics.hawaii.edu/Research/Hackystat/) is one of its major proponents.
While Hackystat itself is kind of inactive the last years, it still remains useful as a common data format for software
telemetry tools. This library allows to create data in a format that is compatible to Hackystat, while being much more
lightweight than the original "SensorBaseClient" from Hackystat.

The Hackystat XML format looks as follows:

 <?xml version="1.0"?>
 <SensorData>
  <Timestamp>2007-04-30T09:00:00.000-10:00</Timestamp>
  <Runtime>2007-04-30T09:00:00.000-10:00</Runtime>
  <Tool>Subversion</Tool>
  <SensorDataType>Commit</SensorDataType>
  <Resource>file://home/johnson/svn/Foo/build.xml</Resource>
  <Owner>http://dasha.ics.hawaii.edu:9876/sensorbase/user/hongbing@hawaii.edu</Owner>
  <Properties>
   <Property>
    <Key>TotalLines</Key>
    <Value>137</Value>
   </Property>
   <Property>
    <Key>LinesAdded</Key>
    <Value>10</Value>
   </Property>
   <Property>
    <Key>LinesDeleted</Key>
    <Value>12</Value>
   </Property>
   <Property>
    <Key>Repository</Key>
    <Value>svn://www.hackystat.org/</Value>
   </Property>
   <Property>
    <Key>RevisionNumber</Key>
    <Value>2345</Value>
   </Property>
   <Property>
    <Key>Author</Key> 
    <Value>johnson</Value>
   </Property>
  </Properties>
 </SensorData>

More information is (hopefully still) available in the Wiki: https://code.google.com/archive/p/hackystat/wikis/SensorDataTypeSpecifications.wiki
