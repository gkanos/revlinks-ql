package at.jku.isse.cloud.revlinks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import at.jku.isse.cloud.artifact.DSClass;
import at.jku.isse.cloud.artifact.DSConnection;
import at.jku.isse.cloud.artifact.DSInstance;
import at.jku.isse.cloud.artifact.DSRevLink;
import at.jku.sea.cloud.Artifact;

public class RevLinkCreation {

	public static void main(String[] args) {
		DSConnection conn = new DSConnection("dos", "mepwd", "my workspace", "some other package");
    	
		DSRevLink revLink = conn.getOrCreateReverseLinkClass();
		
		Collection<Artifact> artifacts = conn.getAllArtifacts();
		for(Artifact artifact : artifacts) {
			DSClass sourceModel = new DSClass(conn, artifact.getType());
			Map<String, Object> props = artifact.getAlivePropertiesMap();
			Multimap<Artifact, String> revLinkRelationNames = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
			for(String key : props.keySet()) {
				Object val = props.get(key);				
				if(val instanceof Artifact) {
					revLinkRelationNames.put((Artifact) val, key);
				}
			}
			for(Map.Entry<Artifact, Collection<String>> entry : revLinkRelationNames.asMap().entrySet()) {
				Artifact target = entry.getKey();
				DSClass targetModel = new DSClass(conn, target.getType());
				revLink.createRevLink(sourceModel, targetModel, new DSInstance(conn, artifact), new DSInstance(conn, target), 
						entry.getValue().toArray(new String[entry.getValue().size()]));
				System.out.println("Created RLink: " + artifact.getId() + " -> " + target.getId() + " [" + entry.getValue().stream().collect(Collectors.joining(",")) + "]");
			}
		}
		System.out.println("Finished.");
		
		/* Queries for the six methodologies:
		 * (2) --L Which car and color correspond to the given link?
		 * (3) -D- Which cars have the color black? Which artifacts are linked to a specific color?
		 * (4) -DL Which car is linked to color black?
		 * (5) S-- Which colors does a specific car have? Which artifacts are directly linked to a specific car?
		 * (6) S-L Which color does a specific car have?
		 * (7) SD- What is the relation between a specific car and a specific color?
		 */
		
		conn.commit("");
	}
}
