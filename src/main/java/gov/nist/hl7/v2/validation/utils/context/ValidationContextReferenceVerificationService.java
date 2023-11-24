package gov.nist.hl7.v2.validation.utils.context;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ValidationContextReferenceVerificationService {
	Map<ReferenceType, Set<String>> ids;
	Document profile;

	public ValidationContextReferenceVerificationService(InputStream profile, InputStream valueSetLibrary) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		this.profile = getXMLDocument(profile);
		initializeProfileIds();
		initializeValueSetIds(valueSetLibrary);
	}

	private Document getXMLDocument(InputStream is) throws IOException, SAXException, ParserConfigurationException {
		if(is != null) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			return builder.parse(is);
		}
		return null;
	}

	private Set<String> getStringValues(Set<String> paths, Document xml) throws XPathExpressionException {
		Set<String> results = new HashSet<>();
		if(xml != null) {
			XPath xPath = XPathFactory.newInstance().newXPath();
			for(String expression : paths) {
				NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xml, XPathConstants.NODESET);
				for(int i = 0; i < nodeList.getLength(); i++) {
					Node n = nodeList.item(i);
					results.add(n.getNodeValue());
				}
			}
		}
		return results;
	}

	private void initializeProfileIds() throws XPathExpressionException {
		this.ids = new HashMap<>();
		this.ids.put(ReferenceType.Message, getStringValues(
				new HashSet<>(Collections.singletonList("/ConformanceProfile/Messages/Message/@ID")),
				profile
		));
		this.ids.put(ReferenceType.Segment, getStringValues(
				new HashSet<>(Collections.singletonList("/ConformanceProfile/Segments/Segment/@ID")),
				profile
		));
		this.ids.put(ReferenceType.Datatype, getStringValues(
				new HashSet<>(Collections.singletonList("/ConformanceProfile/Datatypes/Datatype/@ID")),
				profile
		));
		this.ids.put(ReferenceType.Group, getStringValues(
				new HashSet<>(Collections.singletonList("//Group[ancestor::Message]/@ID")),
				profile
		));
	}

	private void initializeValueSetIds(InputStream valueSetLibrary) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		Document vsLib = getXMLDocument(valueSetLibrary);
		this.ids.put(ReferenceType.ValueSet, getStringValues(
				new HashSet<>(Collections.singletonList("//ValueSetDefinition/@BindingIdentifier")),
				vsLib
		));
	}

	private Set<Reference> getUnknownReferences(Set<String> paths, Document xml, ReferenceType referenceType) throws XPathExpressionException {
		Set<String> known = ids.getOrDefault(referenceType, Collections.emptySet());
		Set<String> references = getStringValues(paths, xml);
		return references.stream()
		                 .filter((ref) -> !known.contains(ref))
		                 .map((ref) -> new Reference(ref, referenceType))
				.collect(Collectors.toSet());
	}

	private Set<Reference> process(Map<ReferenceType, List<String>> paths, Document document) throws XPathExpressionException {
		Set<Reference> collector = new HashSet<>();
		for(ReferenceType type: paths.keySet()) {
			collector.addAll(
					getUnknownReferences(
							new HashSet<>(paths.get(type)),
							document,
							type
					)
			);
		}
		return collector;
	}

	public Set<Reference> getUnknownProfileReferences() throws XPathExpressionException {
		Map<ReferenceType, List<String>> paths = new HashMap<ReferenceType, List<String>>() {{
			put(ReferenceType.Datatype, Arrays.asList(
					"//Field[ancestor::Segment]/@Datatype",
					"//Component[ancestor::Datatype]/@Datatype",
					"//Case[ancestor::Mapping]/@Datatype"
			));
			put(ReferenceType.Segment, Arrays.asList(
					"//Segment[ancestor::Message]/@Ref"
			));
			put(ReferenceType.ValueSet, Arrays.asList(
					"//Component[ancestor::Datatype]/@BindingIdentifier",
					"//Field[ancestor::Segment]/@BindingIdentifier"
			));
		}};
		return process(paths, this.profile);
	}

	public Set<Reference> getUnknownValueSetBindingsReferences(InputStream valueSetBindings) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		Document document = getXMLDocument(valueSetBindings);
		Map<ReferenceType, List<String>> paths = new HashMap<ReferenceType, List<String>>() {{
			put(ReferenceType.Datatype, Arrays.asList(
					"/ValueSetBindingsContext/ValueSetBindings/Datatype/ByID/@ID",
					"/ValueSetBindingsContext/SingleCodeBindings/Datatype/ByID/@ID"
			));
			put(ReferenceType.Segment, Arrays.asList(
					"/ValueSetBindingsContext/ValueSetBindings/Segment/ByID/@ID",
					"/ValueSetBindingsContext/SingleCodeBindings/Segment/ByID/@ID"
			));
			put(ReferenceType.Message, Arrays.asList(
					"/ValueSetBindingsContext/ValueSetBindings/Message/ByID/@ID",
					"/ValueSetBindingsContext/SingleCodeBindings/Message/ByID/@ID"
			));
			put(ReferenceType.ValueSet, Arrays.asList(
					"//Binding/@BindingIdentifier"
			));
		}};
		return process(paths, document);
	}

	public Set<Reference> getUnknownSlicingReferences(InputStream slicings) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		Document document = getXMLDocument(slicings);
		Map<ReferenceType, List<String>> paths = new HashMap<ReferenceType, List<String>>() {{
			put(ReferenceType.Datatype, Arrays.asList(
					"//Slice[ancestor::FieldSlicing]/@Ref"
			));
			put(ReferenceType.Segment, Arrays.asList(
					"//Slice[ancestor::SegmentSlicing]/@Ref",
					"/ProfileSlicing/FieldSlicing/SegmentContext/@ID"
			));
			put(ReferenceType.Message, Arrays.asList(
					"/ProfileSlicing/SegmentSlicing/Message/@ID"
			));
			put(ReferenceType.Group, Arrays.asList(
					"//GroupContext/@ID"
			));
		}};
		return process(paths, document)
				.stream()
				.filter((ref) -> !ref.getType().equals(ReferenceType.Group) || !ids.get(ReferenceType.Message).contains(ref.id))
				.collect(Collectors.toSet());
	}

	public Set<Reference> getUnknownConstraintsReferences(InputStream constraints) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		Document document = getXMLDocument(constraints);
		Map<ReferenceType, List<String>> paths = new HashMap<ReferenceType, List<String>>() {{
			put(ReferenceType.Datatype, Arrays.asList(
					"/ConformanceContext/Predicates/Datatype/ByID/@ID",
					"/ConformanceContext/Constraints/Datatype/ByID/@ID"
			));
			put(ReferenceType.Segment, Arrays.asList(
					"/ConformanceContext/Predicates/Segment/ByID/@ID",
					"/ConformanceContext/Constraints/Segment/ByID/@ID"
			));
			put(ReferenceType.Message, Arrays.asList(
					"/ConformanceContext/Predicates/Message/ByID/@ID",
					"/ConformanceContext/Constraints/Message/ByID/@ID"
			));
			put(ReferenceType.Group, Arrays.asList(
					"/ConformanceContext/Predicates/Group/ByID/@ID",
					"/ConformanceContext/Constraints/Group/ByID/@ID"
			));
		}};
		return process(paths, document);
	}

	public Set<Reference> getUnknownCoConstraintsReferences(InputStream coconstraints) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		Document document = getXMLDocument(coconstraints);
		Map<ReferenceType, List<String>> paths = new HashMap<ReferenceType, List<String>>() {{
			put(ReferenceType.Message, Arrays.asList(
					"/CoConstraintContext/ByMessage/@ID"
			));
			put(ReferenceType.ValueSet, Arrays.asList(
					"//Binding/@BindingIdentifier"
			));
		}};
		return process(paths, document);
	}

	public static Set<Reference> getUnknownReferences(
			InputStream profile,
			InputStream valueSetLibrary,
			Set<InputStream> constraints,
			InputStream valueSetBindings,
			InputStream coConstraints,
			InputStream slicing
	) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		ValidationContextReferenceVerificationService checker = new ValidationContextReferenceVerificationService(profile, valueSetLibrary);
		Set<Reference> unknown = new HashSet<>(checker.getUnknownProfileReferences());
		for(InputStream constraint: constraints) {
			unknown.addAll(checker.getUnknownConstraintsReferences(constraint));
		}
		unknown.addAll(checker.getUnknownValueSetBindingsReferences(valueSetBindings));
		unknown.addAll(checker.getUnknownCoConstraintsReferences(coConstraints));
		unknown.addAll(checker.getUnknownSlicingReferences(slicing));
		return unknown;
	}
}
