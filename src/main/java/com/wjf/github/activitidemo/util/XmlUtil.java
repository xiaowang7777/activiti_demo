package com.wjf.github.activitidemo.util;

import com.wjf.github.activitidemo.entity.LineInfo;
import com.wjf.github.activitidemo.entity.NodeBaseInfo;
import com.wjf.github.activitidemo.entity.NodeInfo;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

public class XmlUtil {

	private static final String BPMN_SHAPE_PREFIX = "Shape-";

	private static final String BPMN_EDGE_PREFIX = "BPMNEdge_";

	private static final String BPMN_BACKGROUND_COLOR = "#3C3F41";

	private static final Double BPMN_IMAGE_WIDTH = 842.4;

	private static final Double BPMN_IMAGE_HEIGHT = 1195.2;

	private static final Double BPMN_IMAGE_ABLE_WIDTH = 832.4;

	private static final Double BPMN_IMAGE_ABLE_HEIGHT = 1185.2;

	private static final Double BPMN_IMAGE_ABLE_X = 5.0;

	private static final Double BPMN_IMAGE_ABLE_Y = 5.0;

	private XmlUtil() {
	}

	private Element createDefinitionElement(Document document, String id, String name) {
		return document.addElement("definitions")
				.addAttribute("xmlns", "http://www.omg.org/spec/BPMN/20100524/MODEL")
				.addAttribute("xmlns:activiti", "http://activiti.org/bpmn")
				.addAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI")
				.addAttribute("xmlns:omgdc", "http://www.omg.org/spec/DD/20100524/DC")
				.addAttribute("xmlns:omgdi", "http://www.omg.org/spec/DD/20100524/DI")
				.addAttribute("xmlns:tns", "http://www.activiti.org/test")
				.addAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema")
				.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
				.addAttribute("expressionLanguage", "http://www.w3.org/1999/XPath")
				.addAttribute("id", id)
				.addAttribute("name", name)
				.addAttribute("targetNamespace", "http://www.activiti.org/test")
				.addAttribute("typeLanguage", "http://www.w3.org/2001/XMLSchema")
				.addNamespace("activiti","http://activiti.org/bpmn")
				.addNamespace("bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI")
				.addNamespace("omgdc", "http://www.omg.org/spec/DD/20100524/DC")
				.addNamespace("omgdi", "http://www.omg.org/spec/DD/20100524/DI")
				.addNamespace("tns", "http://www.activiti.org/test")
				.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema")
				.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
	}

	private Element createProcessElement(Element definitions, String processId) {
		return definitions.addElement("process")
				.addAttribute("id", processId)
				.addAttribute("isClosed", "false")
				.addAttribute("isExecutable", "true")
				.addAttribute("processType", "None");
	}

	private Element createStartEventElement(Element process, String id, String name) {
		return process.addElement("startEvent")
				.addAttribute("id", id)
				.addAttribute("name", name);
	}

	private Element createEndEventElement(Element process, String id, String name) {
		return process.addElement("endEvent")
				.addAttribute("id", id)
				.addAttribute("name", name);
	}

	private Element createUserTaskElement(Element process, String id, String name, String exclusive) {
		return process.addElement("userTask")
				.addAttribute("activiti:exclusive", exclusive)
				.addAttribute("id", id)
				.addAttribute("name", name);
	}

	private Element createSequenceFlowElement(Element process, String id, String from, String to) {
		return process.addElement("sequenceFlow")
				.addAttribute("id", id)
				.addAttribute("sourceRef", from)
				.addAttribute("targetRef", to);
	}

	private Element createBPMNDiagramElement(Element definitions, String id, String name) {
		return definitions.addElement("bpmndi:BPMNDiagram")
				.addAttribute("documentation", "background=" + BPMN_BACKGROUND_COLOR + ";count=1;horizontalcount=1;orientation=0;width=" + BPMN_IMAGE_WIDTH + ";height=" + BPMN_IMAGE_HEIGHT + ";imageableWidth=" + BPMN_IMAGE_ABLE_WIDTH + ";imageableHeight=" + BPMN_IMAGE_ABLE_HEIGHT + ";imageableX=" + BPMN_IMAGE_ABLE_X + ";imageableY=" + BPMN_IMAGE_ABLE_Y + "")
				.addAttribute("id", id)
				.addAttribute("name", name);
	}

	private Element createBPMNPlaneElement(Element bpmnDiagram, String processId) {
		return bpmnDiagram.addElement("bpmndi:BPMNPlane")
				.addAttribute("bpmnElement", processId);
	}

	private Element createBPMNShapeElement(Element BPMNPlane, String id) {
		return BPMNPlane.addElement("bpmndi:BPMNShape")
				.addAttribute("bpmnElement", id)
				.addAttribute("id", BPMN_SHAPE_PREFIX + id);
	}

	private Element createBPMNEdgeElement(Element BPMNPlane, String id, String from, String to) {
		return BPMNPlane.addElement("bpmndi:BPMNEdge")
				.addAttribute("bpmnElement", id)
				.addAttribute("id", BPMN_EDGE_PREFIX + id)
				.addAttribute("sourceElement", from)
				.addAttribute("targetElement", to);
	}

	private Element createBoundsElement(Element BPMNShape, String height, String width, String x, String y) {
		return BPMNShape.addElement("omgdc:Bounds")
				.addAttribute("height", height)
				.addAttribute("width", width)
				.addAttribute("x", x)
				.addAttribute("y", y);
	}

	private Element createBPMNLabel(Element BPMNShape) {
		return BPMNShape.addElement("bpmndi:BPMNLabel");
	}

	private Element createWayPoint(Element BPMNEdge, String x, String y) {
		return BPMNEdge.addElement("omgdi:waypoint")
				.addAttribute("x", x)
				.addAttribute("y", y);
	}

	public static Document createDocument(String processId, List<NodeBaseInfo> list, Double width, Double height) {
		XmlUtil xmlUtil = new XmlUtil();
		Document document = DocumentHelper.createDocument();
		Element rootElement = xmlUtil.createDefinitionElement(document, "m1588216735437", "");
		Element processElement = xmlUtil.createProcessElement(rootElement, processId);
		Element diagramElement = xmlUtil.createBPMNDiagramElement(rootElement, "Diagram-_1", "NEW Diagram");
		Element bpmnPlaneElement = xmlUtil.createBPMNPlaneElement(diagramElement, processId);

		double width_ratio = width / BPMN_IMAGE_ABLE_WIDTH;
		double height_ratio = height / BPMN_IMAGE_ABLE_HEIGHT;


		NodeInfo tmpNodeInfo;
		LineInfo tmpLineInfo;

//		生成定义节点
		for (NodeBaseInfo nodeBaseInfo : list) {
			if (nodeBaseInfo instanceof NodeInfo) {
				tmpNodeInfo = (NodeInfo) nodeBaseInfo;
				if (tmpNodeInfo.getType() == 1) {
					xmlUtil.createStartEventElement(processElement, tmpNodeInfo.getId(), tmpNodeInfo.getName());
				} else if (tmpNodeInfo.getType() == 2) {
					xmlUtil.createEndEventElement(processElement, tmpNodeInfo.getId(), tmpNodeInfo.getName());
				} else {
					xmlUtil.createUserTaskElement(processElement, tmpNodeInfo.getId(), tmpNodeInfo.getName(), "true");
				}
				Element shapeElement = xmlUtil.createBPMNShapeElement(bpmnPlaneElement, tmpNodeInfo.getId());
				xmlUtil.createBoundsElement(shapeElement, tmpNodeInfo.getHeight() * height_ratio + "", tmpNodeInfo.getWidth() * width_ratio + "", tmpNodeInfo.getX() * width_ratio + "", tmpNodeInfo.getY() * height_ratio + "");
				Element labelElement = xmlUtil.createBPMNLabel(shapeElement);
				xmlUtil.createBoundsElement(labelElement, tmpNodeInfo.getHeight() * height_ratio + "", tmpNodeInfo.getWidth() * width_ratio + "", "0", "0");
			} else {
				tmpLineInfo = (LineInfo) nodeBaseInfo;
				xmlUtil.createSequenceFlowElement(processElement, tmpLineInfo.getId(), tmpLineInfo.getFrom(), tmpLineInfo.getTo());
				Element edgeElement = xmlUtil.createBPMNEdgeElement(bpmnPlaneElement, tmpLineInfo.getId(), tmpLineInfo.getFrom(), tmpLineInfo.getTo());
				xmlUtil.createWayPoint(edgeElement, tmpLineInfo.getX() * height_ratio + "", tmpLineInfo.getY() * width_ratio + "");
				xmlUtil.createWayPoint(edgeElement, tmpLineInfo.getHeight() * height_ratio + "", tmpLineInfo.getWidth() * width_ratio + "");
				Element labelElement = xmlUtil.createBPMNLabel(edgeElement);
				xmlUtil.createBoundsElement(labelElement, tmpLineInfo.getHeight() * height_ratio + "", tmpLineInfo.getWidth() * width_ratio + "", "0", "0");
			}

		}

		return document;
	}
}
