package com.test.json;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CellSetSerializerStacked extends JsonSerializer<CellSet> {

    @Override
    public Class<CellSet> handledType() {
        return CellSet.class;
    }

    @Override
    public void serialize(CellSet cellSet, JsonGenerator json, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        json.writeStartObject();
        json.writeFieldName("metadata");
        json.writeStartObject();
        json.writeFieldName("xlabel");
        json.writeString(cellSet.getAxes().get(1).getPositions().get(0).getMembers().get(0).getLevel().getName());

        json.writeFieldName("ylabel");
        json.writeString("Count");

        List<CellSetAxis> axes = cellSet.getAxes();
        int i=1;
        for(CellSetAxis axis : axes) {
            String axisName = "axis"+ i++;
            List<Position> positions = axis.getPositions();
            String axisOrdinal = axis.getAxisOrdinal().getCaption(Locale.US);
            System.out.println("axisname ordinal = " + axisOrdinal);
            //---------------------------------------------------------------------
            json.writeFieldName(axisName + "Definition");
            json.writeStartObject();     // axis definition object start
            for(Position position : positions) {
                 List<Member> members = position.getMembers();
                 for(Member member : members) {
                     json.writeFieldName(member.getCaption());
                     json.writeString(member.getUniqueName());
                 }
            }
            json.writeEndObject();    // axis definition object end
            //---------------------------------------------------------------------
            json.writeFieldName(axisName); // axis array names object start
            json.writeStartArray();
            for(Position position : positions) {
                List<Member> members = position.getMembers();
                for(Member member : members) {
                    json.writeString(member.getCaption());
                }
            }
            json.writeEndArray();            // axis array names object start
            //---------------------------------------------------------------------
            json.writeFieldName(axisName+"parent"); // axis array names object start
            json.writeString(positions.get(0).getMembers().get(0).getParentMember().getUniqueName());
        }
        json.writeEndObject();
        json.writeFieldName("values");
        json.writeStartArray();
        write(json, cellSet, axes);
        json.writeEndArray();
        json.writeEndObject();
    }

    // will work only for 2 axes (not more, nor less)
    private void write(final JsonGenerator json, final CellSet cs, final List<CellSetAxis> axes) throws IOException {
        CellSetAxis axisabove = axes.get(1);
        int cellIndex = 0;
        for(int j=0; j< axisabove.getPositionCount(); j++) {
            json.writeStartObject();
           /* json.writeFieldName(axisabove.getAxisMetaData().getHierarchies().get(0).getCaption());*/
            json.writeFieldName("axis2");
            json.writeString(axisabove.getPositions().get(j).getMembers().get(0).getCaption());
            CellSetAxis axis = axes.get(0);
            for(int i = 0; i < axis.getPositionCount(); ++i) {
                    json.writeFieldName(axis.getPositions().get(i).getMembers().get(0).getCaption());
                    String str = cs.getCell(cellIndex++).getFormattedValue();
                try {
                    json.writeNumber((Long) NumberFormat.getNumberInstance(java.util.Locale.US).parse(str));
                } catch (ParseException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            json.writeEndObject();
        }
    }

}
