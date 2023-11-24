package gov.nist.hl7.v2.validation.utils.context;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UnknownReferencesTest {

	private ValidationContextReferenceVerificationService verificationService;

	@BeforeAll
	public void init() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
		verificationService = new ValidationContextReferenceVerificationService(
				UnknownReferencesTest.class.getResourceAsStream("/example/Profile.xml"),
				UnknownReferencesTest.class.getResourceAsStream("/example/ValueSets.xml")
		);
	}

	@Test
	public void detectUnknownProfileReferences() throws XPathExpressionException {
		Set<Reference> references = verificationService.getUnknownProfileReferences();
		List<Reference> expected = Arrays.asList(
				new Reference("UNKNOWN_PROFILE_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_PROFILE_DM_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_PROFILE_FIELD_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_PROFILE_COMPONENT_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_PROFILE_COMPONENT_VS_REFERENCE", ReferenceType.ValueSet),
				new Reference("UNKNOWN_PROFILE_FIELD_VS_REFERENCE", ReferenceType.ValueSet)
		);
		assertTrue(
				references.containsAll(expected) && expected.size() == references.size()
		);
	}

	@Test
	public void detectUnknownConstraintsReferences() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
		Set<Reference> references = verificationService.getUnknownConstraintsReferences(
				UnknownReferencesTest.class.getResourceAsStream("/example/Constraints.xml")
		);
		List<Reference> expected = Arrays.asList(
				new Reference("UNKNOWN_PREDICATE_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_PREDICATE_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_PREDICATE_GROUP_REFERENCE", ReferenceType.Group),
				new Reference("UNKNOWN_PREDICATE_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_CONSTRAINT_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_CONSTRAINT_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_CONSTRAINT_GROUP_REFERENCE", ReferenceType.Group),
				new Reference("UNKNOWN_CONSTRAINT_MESSAGE_REFERENCE", ReferenceType.Message)
		);
		assertTrue(
				references.containsAll(expected) && expected.size() == references.size()
		);
	}

	@Test
	public void detectUnknownValueSetBindingsReferences() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
		Set<Reference> references = verificationService.getUnknownValueSetBindingsReferences(
				UnknownReferencesTest.class.getResourceAsStream("/example/ValueSetBindings.xml")
		);
		List<Reference> expected = Arrays.asList(
				new Reference("UNKNOWN_BINDING_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_BINDING_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_BINDING_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_SINGLE_CODE_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_SINGLE_CODE_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_SINGLE_CODE_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_BINDING_DATATYPE_VALUESET_REFERENCE", ReferenceType.ValueSet),
				new Reference("UNKNOWN_BINDING_SEGMENT_VALUESET_REFERENCE", ReferenceType.ValueSet),
				new Reference("UNKNOWN_BINDING_MESSAGE_VALUESET_REFERENCE_ONE", ReferenceType.ValueSet),
				new Reference("UNKNOWN_BINDING_MESSAGE_VALUESET_REFERENCE_TWO", ReferenceType.ValueSet)
		);
		assertTrue(
				references.containsAll(expected) && expected.size() == references.size()
		);
	}

	@Test
	public void detectUnknownSlicingReferences() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
		Set<Reference> references = verificationService.getUnknownSlicingReferences(
				UnknownReferencesTest.class.getResourceAsStream("/example/Slicings.xml")
		);
		List<Reference> expected = Arrays.asList(
				new Reference("UNKNOWN_SLICING_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_SLICING_GROUP_REFERENCE", ReferenceType.Group),
				new Reference("UNKNOWN_SLICING_SEGMENT_ASSERTION_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_SLICING_SEGMENT_OCCURRENCE_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_SLICING_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_SLICING_DATATYPE_OCCURRENCE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_SLICING_DATATYPE_ASSERTION_REFERENCE", ReferenceType.Datatype)
		);
		System.out.println(references);
		assertTrue(
				references.containsAll(expected) && expected.size() == references.size()
		);
	}

	@Test
	public void detectUnknownCoConstraintReferences() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
		Set<Reference> references = verificationService.getUnknownCoConstraintsReferences(
				UnknownReferencesTest.class.getResourceAsStream("/example/CoConstraints.xml")
		);
		List<Reference> expected = Arrays.asList(
				new Reference("UNKNOWN_CC_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_CC_VS_REFERENCE", ReferenceType.ValueSet)
		);
		assertTrue(
				references.containsAll(expected) && expected.size() == references.size()
		);
	}

	@Test
	public void detectUnknownBundleReferences() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
		Set<Reference> references = ValidationContextReferenceVerificationService.getUnknownReferences(
				UnknownReferencesTest.class.getResourceAsStream("/example/Profile.xml"),
				UnknownReferencesTest.class.getResourceAsStream("/example/ValueSets.xml"),
				new HashSet<>(Arrays.asList(UnknownReferencesTest.class.getResourceAsStream("/example/Constraints.xml"))),
				UnknownReferencesTest.class.getResourceAsStream("/example/ValueSetBindings.xml"),
				UnknownReferencesTest.class.getResourceAsStream("/example/CoConstraints.xml"),
				UnknownReferencesTest.class.getResourceAsStream("/example/Slicings.xml")
		);

		// Profile
		List<Reference> expected = new ArrayList<>();


		expected.addAll(Arrays.asList(
				new Reference("UNKNOWN_PROFILE_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_PROFILE_DM_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_PROFILE_FIELD_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_PROFILE_COMPONENT_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_PROFILE_COMPONENT_VS_REFERENCE", ReferenceType.ValueSet),
				new Reference("UNKNOWN_PROFILE_FIELD_VS_REFERENCE", ReferenceType.ValueSet)
		));

		// Constraints
		expected.addAll(Arrays.asList(
				new Reference("UNKNOWN_PREDICATE_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_PREDICATE_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_PREDICATE_GROUP_REFERENCE", ReferenceType.Group),
				new Reference("UNKNOWN_PREDICATE_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_CONSTRAINT_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_CONSTRAINT_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_CONSTRAINT_GROUP_REFERENCE", ReferenceType.Group),
				new Reference("UNKNOWN_CONSTRAINT_MESSAGE_REFERENCE", ReferenceType.Message)
		));

		// Vs Binding
		expected.addAll(Arrays.asList(
				new Reference("UNKNOWN_BINDING_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_BINDING_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_BINDING_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_SINGLE_CODE_DATATYPE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_SINGLE_CODE_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_SINGLE_CODE_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_BINDING_DATATYPE_VALUESET_REFERENCE", ReferenceType.ValueSet),
				new Reference("UNKNOWN_BINDING_SEGMENT_VALUESET_REFERENCE", ReferenceType.ValueSet),
				new Reference("UNKNOWN_BINDING_MESSAGE_VALUESET_REFERENCE_ONE", ReferenceType.ValueSet),
				new Reference("UNKNOWN_BINDING_MESSAGE_VALUESET_REFERENCE_TWO", ReferenceType.ValueSet)
		));

		// Slicing
		expected.addAll(Arrays.asList(
				new Reference("UNKNOWN_SLICING_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_SLICING_GROUP_REFERENCE", ReferenceType.Group),
				new Reference("UNKNOWN_SLICING_SEGMENT_ASSERTION_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_SLICING_SEGMENT_OCCURRENCE_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_SLICING_SEGMENT_REFERENCE", ReferenceType.Segment),
				new Reference("UNKNOWN_SLICING_DATATYPE_OCCURRENCE_REFERENCE", ReferenceType.Datatype),
				new Reference("UNKNOWN_SLICING_DATATYPE_ASSERTION_REFERENCE", ReferenceType.Datatype)
		));

		// Co-Constraints
		expected.addAll(Arrays.asList(
				new Reference("UNKNOWN_CC_MESSAGE_REFERENCE", ReferenceType.Message),
				new Reference("UNKNOWN_CC_VS_REFERENCE", ReferenceType.ValueSet)
		));

		assertTrue(
				references.containsAll(expected) && expected.size() == references.size()
		);
	}
}
