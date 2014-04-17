package mil.nga.giat.mage.sdk.datastore.layer;

import java.util.ArrayList;
import java.util.Collection;

import mil.nga.giat.mage.sdk.datastore.staticfeature.StaticFeature;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "layers")
public class Layer implements Comparable<Layer> {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(unique = true, columnName = "remote_id")
    private String remoteId;

    @DatabaseField
    private String type;

    @DatabaseField
    private String name;

    @DatabaseField
    private boolean loaded = false;

    /**
     * Do NOT eager load the features!
     * 
     */
    @ForeignCollectionField(eager = false)
    private Collection<StaticFeature> staticFeatures = new ArrayList<StaticFeature>();

    public Layer() {
        // ORMLite needs a no-arg constructor
    }

    public Layer(String remoteId, String type, String name) {
        super();
        this.remoteId = remoteId;
        this.type = type;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public Collection<StaticFeature> getStaticFeatures() {
        return staticFeatures;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int compareTo(Layer another) {
        return new CompareToBuilder().append(this.id, another.id).append(this.remoteId, another.remoteId).toComparison();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((remoteId == null) ? 0 : remoteId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Layer other = (Layer) obj;
        
        Long otherId = other.id;
        String otherRemote = other.remoteId;
        
        if (!super.equals(obj)) {
            Log.i("layer", "super is not equal");
        }
        
        if (!id.equals(otherId)) {
            Log.i("layer", "ids are not equal");
        }
        
        if (!remoteId.equals(otherRemote)) {
            Log.i("layer", "ids are not equal");
        }
         
        boolean eq = new EqualsBuilder().append(id, other.id).append(remoteId, other.remoteId).isEquals();
        return eq;
    }
}
