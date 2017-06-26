package synchronoss.data.countly.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by srya0001 on 4/28/2016.
 */
 @ApiModel(value = "User Data", description = "All values are optional and currently you can pass this information about user")
public class UserDetails {

    @ApiModelProperty( value = "name",
            name = "Name",
            access = "public",
            dataType = "string")
    public String       name;

    @ApiModelProperty( value = "username",
            name = "Username",
            access = "public",
            dataType = "string")
    public String       username;

    @ApiModelProperty( value = "email",
            name = "Email",
            access = "public",
            dataType = "string")
    public String       email;

    @ApiModelProperty( value = "organization",
            name = "Organization",
            access = "public",
            dataType = "string")
    public String       organization;

    @ApiModelProperty( value = "phone",
            name = "Phone",
            access = "public",
            dataType = "string")
    public String       phone;

    @ApiModelProperty( value = "picture",
            name = "Picture",
            notes = "URL to user picture",
            access = "public",
            dataType = "string")
    public String       picture;

    @ApiModelProperty( value = "gender",
            name = "Gender",
            access = "public",
            dataType = "string")
    public String       gender;

    @ApiModelProperty( value = "byear",
            name = "Birth Year",
            access = "public",
            dataType = "string")
    public String       byear;

    public Map<String, String> custom;

    public UserDetails(){

    }

    public UserDetails(byte[] serialized){
        this.setFromBytes(serialized);
    }

    public String toString() {
        String seg = "custom:";
        if(custom != null) {
            if(custom.size() > 0 ) {
                for (Map.Entry<String, String> e : this.custom.entrySet()) {
                    String tmp = "[" + (e.getKey() == null ? "<null>" : e.getKey()) + ":"
                            + (e.getValue() == null ? "<null>" : e.getValue()) + "] ";
                    seg += tmp;
                }
            } else
                seg += "<empty>";
        } else {
            seg += ": <null>";
        }
        return
            "name: " + (name ==  null ? "<null>" : name) +
            ", username: " + (username ==  null ? "<null>" : username) +
            ", email: " + (email ==  null ? "<null>" : email) +
            ", organization: " + (organization ==  null ? "<null>" : organization) +
            ", phone: " + (phone ==  null ? "<null>" : phone) +
            ", picture: " + (picture ==  null ? "<null>" : picture) +
            ", gender: " + (gender ==  null ? "<null>" : gender) +
            ", byear: " + (byear ==  null ? "<null>" : byear) + ", "
                    + seg;

    }

    public byte[] getBytes() {
        ByteBuffer customByteBuffer = SerializationHelper.serializeMap(custom);

        byte[] name_bytes           = name == null ? null : name.getBytes();
        byte[] username_bytes       = username == null ? null : username.getBytes();
        byte[] email_bytes          = email == null ? null : email.getBytes();
        byte[] organization_bytes   = organization == null ? null : organization.getBytes();
        byte[] phone_bytes          = phone == null ? null : phone.getBytes();
        byte[] picture_bytes        = picture == null ? null : picture.getBytes();
        byte[] gender_bytes         = gender == null ? null : gender.getBytes();
        byte[] byear_bytes          = byear  == null ? null : byear.getBytes();

        int size =
                (name_bytes == null ? 0 : name_bytes.length) + 4 +
                (username_bytes == null ? 0 : username_bytes.length) + 4 +
                (email_bytes == null ? 0 : email_bytes.length) + 4 +
                (organization_bytes == null ? 0 : organization_bytes.length) + 4 +
                (phone_bytes == null ? 0 : phone_bytes.length) + 4 +
                (picture_bytes == null ? 0 : picture_bytes.length) + 4 +
                (gender_bytes == null ? 0 : gender_bytes.length) + 4 +
                (byear_bytes == null ? 0 : byear_bytes.length) + 4 +
                (customByteBuffer == null ? 0 : customByteBuffer.array().length) + 4;


        ByteBuffer output = ByteBuffer.allocate(size);

        SerializationHelper.put(output, name_bytes);
        SerializationHelper.put(output, username_bytes);
        SerializationHelper.put(output, email_bytes);
        SerializationHelper.put(output, organization_bytes);
        SerializationHelper.put(output, phone_bytes);
        SerializationHelper.put(output, picture_bytes);
        SerializationHelper.put(output, gender_bytes);
        SerializationHelper.put(output, byear_bytes);

        if(customByteBuffer != null){
            output.putInt(customByteBuffer.array().length);
            output.put(customByteBuffer.array());
        }   else output.putInt(0);

        return output.array();
    }
    public void setFromBytes(byte[] serialized) {
        ByteBuffer input = ByteBuffer.wrap(serialized);
        name    = SerializationHelper.getString(input);
        username = SerializationHelper.getString(input);
        email   = SerializationHelper.getString(input);
        organization = SerializationHelper.getString(input);
        phone   = SerializationHelper.getString(input);
        picture = SerializationHelper.getString(input);
        gender  = SerializationHelper.getString(input);
        byear   = SerializationHelper.getString(input);

        int customBytesSize = input.getInt();
        if(customBytesSize > 0){
            byte[] map_bytes = new byte[customBytesSize];
            input.get(map_bytes);
            ByteBuffer mapByteBuffer =  ByteBuffer.wrap(map_bytes);
            custom = SerializationHelper.getMap(mapByteBuffer);
        } else
            custom = null;
    }

}
