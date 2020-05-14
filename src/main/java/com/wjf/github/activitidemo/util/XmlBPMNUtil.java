package com.wjf.github.activitidemo.util;

import com.wjf.github.activitidemo.entity.LineInfo;
import com.wjf.github.activitidemo.entity.NodeBaseInfo;
import com.wjf.github.activitidemo.entity.NodeInfo;
import org.dom4j.*;

import java.util.ArrayList;
import java.util.List;

public class XmlBPMNUtil {

	private static final String BPMN_SHAPE_PREFIX = "Shape-";

	private static final String BPMN_EDGE_PREFIX = "BPMNEdge_";

	private static final String BPMN_BACKGROUND_COLOR = "#FFF";

	private static final Double BPMN_IMAGE_WIDTH = 842.4;

	private static final Double BPMN_IMAGE_HEIGHT = 1195.2;

	private static final Double BPMN_IMAGE_ABLE_WIDTH = 832.4;

	private static final Double BPMN_IMAGE_ABLE_HEIGHT = 1185.2;

	private static final Double BPMN_IMAGE_ABLE_X = 5.0;

	private static final Double BPMN_IMAGE_ABLE_Y = 5.0;

	private XmlBPMNUtil() {
	}

	private Element createDefinitionElement(Document document, String id, String name) {

		return document.addElement("definitions", "http://www.omg.org/spec/BPMN/20100524/MODEL")
				.addAttribute("expressionLanguage", "http://www.w3.org/1999/XPath")
				.addAttribute("id", id)
				.addAttribute("name", name)
				.addAttribute("targetNamespace", "http://www.activiti.org/test")
				.addAttribute("typeLanguage", "http://www.w3.org/2001/XMLSchema")
				.addNamespace("activiti", "http://activiti.org/bpmn")
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

	private Element createUserTaskElement(Element process, String id, String name, String exclusive, String users, String formKey) {
		return process.addElement("userTask")
				.addAttribute("activiti:exclusive", exclusive)
				.addAttribute("id", id)
				.addAttribute("name", name)
				.addAttribute("activiti:candidateUsers", users)
				.addAttribute("activiti:formKey", formKey);
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
		XmlBPMNUtil xmlBPMNUtil = new XmlBPMNUtil();
		Document document = DocumentHelper.createDocument();
		Element rootElement = xmlBPMNUtil.createDefinitionElement(document, "m1588216735437", "");
		Element processElement = xmlBPMNUtil.createProcessElement(rootElement, "_" + processId);
		Element diagramElement = xmlBPMNUtil.createBPMNDiagramElement(rootElement, "Diagram-_1", "NEW Diagram");
		Element bpmnPlaneElement = xmlBPMNUtil.createBPMNPlaneElement(diagramElement, "_" + processId);

		double width_ratio = width / BPMN_IMAGE_ABLE_WIDTH;
		double height_ratio = height / BPMN_IMAGE_ABLE_HEIGHT;


		NodeInfo tmpNodeInfo;
		LineInfo tmpLineInfo;

//		生成定义节点
		for (NodeBaseInfo nodeBaseInfo : list) {
			if (nodeBaseInfo instanceof NodeInfo) {
				tmpNodeInfo = (NodeInfo) nodeBaseInfo;
				if (tmpNodeInfo.getType() == 1) {
					xmlBPMNUtil.createStartEventElement(processElement, "_" + tmpNodeInfo.getId(), tmpNodeInfo.getName());
				} else if (tmpNodeInfo.getType() == 2) {
					xmlBPMNUtil.createEndEventElement(processElement, "_" + tmpNodeInfo.getId(), tmpNodeInfo.getName());
				} else {
					xmlBPMNUtil.createUserTaskElement(processElement, "_" + tmpNodeInfo.getId(), tmpNodeInfo.getName(), "true", "#{" + tmpNodeInfo.getUserGroup() + "}", tmpNodeInfo.getPath() + "");
				}
				Element shapeElement = xmlBPMNUtil.createBPMNShapeElement(bpmnPlaneElement, "_" + tmpNodeInfo.getId());
				xmlBPMNUtil.createBoundsElement(shapeElement, Double.parseDouble(tmpNodeInfo.getHeight().replaceAll("px", "")) * height_ratio + "", Double.parseDouble(tmpNodeInfo.getWidth().replaceAll("px", "")) * width_ratio + "", Double.parseDouble(tmpNodeInfo.getTop().replaceAll("px", "")) * width_ratio + "", Double.parseDouble(tmpNodeInfo.getLeft().replaceAll("px", "")) * height_ratio + "");
				Element labelElement = xmlBPMNUtil.createBPMNLabel(shapeElement);
				xmlBPMNUtil.createBoundsElement(labelElement, Double.parseDouble(tmpNodeInfo.getHeight().replaceAll("px", "")) * height_ratio + "", Double.parseDouble(tmpNodeInfo.getWidth().replaceAll("px", "")) * width_ratio + "", "0", "0");
			} else {
				tmpLineInfo = (LineInfo) nodeBaseInfo;
				xmlBPMNUtil.createSequenceFlowElement(processElement, "_" + tmpLineInfo.getId(), "_" + tmpLineInfo.getFrom(), "_" + tmpLineInfo.getTo());
				Element edgeElement = xmlBPMNUtil.createBPMNEdgeElement(bpmnPlaneElement, "_" + tmpLineInfo.getId(), "_" + tmpLineInfo.getFrom(), "_" + tmpLineInfo.getTo());
				xmlBPMNUtil.createWayPoint(edgeElement, Double.parseDouble(tmpLineInfo.getTop().replaceAll("px", "")) * height_ratio + "", Double.parseDouble(tmpLineInfo.getLeft().replaceAll("px", "")) * width_ratio + "");
				xmlBPMNUtil.createWayPoint(edgeElement, Double.parseDouble(tmpLineInfo.getHeight().replaceAll("px", "")) * height_ratio + "", Double.parseDouble(tmpLineInfo.getWidth().replaceAll("px", "")) * width_ratio + "" + "");
				Element labelElement = xmlBPMNUtil.createBPMNLabel(edgeElement);
				xmlBPMNUtil.createBoundsElement(labelElement, "0", "0", "0", "0");
			}

		}

		return document;
	}

	public static Document createDocument(String processId, MyList<NodeInfo> nodeList, List<LineInfo> lineList, Double width, Double height) {

		int index = 0;

		List<NodeBaseInfo> list = new ArrayList<>();

		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getType() == 1) {
				NodeInfo nodeInfo = nodeList.reject(i);
				nodeInfo.setTop(60 + "px");
				nodeInfo.setLeft(200 * index + "px");
				i += 1;
				list.add(nodeInfo);
				create(nodeInfo, lineList, nodeList, list, i);
				break;
			}
		}

		list.addAll(lineList);

		return createDocument(processId, list, width, height);
	}

	private static void create(NodeInfo source, List<LineInfo> lineList, MyList<NodeInfo> nodeList, List<NodeBaseInfo> list, int index) {
		for (LineInfo lineInfo : lineList) {
			if (lineInfo.getFrom().equals(source.getId())) {
				for (int i = 0; i < nodeList.size(); i++) {
					if (nodeList.get(i).getId().equals(lineInfo.getTo())) {
						NodeInfo target = nodeList.reject(i);
						target.setLeft(200 * index + "px");
						target.setTop(60 + "px");
						lineInfo.setHeight(source.getTop());
						lineInfo.setWidth(source.getLeft());
						lineInfo.setLeft(target.getLeft());
						lineInfo.setTop(target.getTop());
						index += 1;
						list.add(target);
						create(target, lineList, nodeList, list, index);
						return;
					}
				}
			}
		}
	}
}
