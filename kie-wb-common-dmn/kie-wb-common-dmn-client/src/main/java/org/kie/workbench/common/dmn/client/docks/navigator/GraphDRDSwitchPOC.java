/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.MultiDRDControl;
import org.kie.workbench.common.dmn.client.shape.factory.DMNShapeFactory;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasView;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

public class GraphDRDSwitchPOC {

    @Inject
    private DMNGraphUtils dmnGraphUtils;

    @Inject
    private DMNDiagramUtils dmnDiagramUtils;

    @Inject
    private DMNShapeFactory factory;

    @Inject
    private TextPropertyProviderFactory textPropertyProviderFactory;

    private List<DRDNode> repositoryOfNodes = new ArrayList<>();

    MultiDRDControl multiDRDControl;

    public void setMultiDRDControl(final MultiDRDControl multiDRDControl) {
        this.multiDRDControl = multiDRDControl;
    }

//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================

//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================
//===============================================

    public void hideNodes(final Integer i) {
        final List<DRDNode> globalRepositoryOfNodes = getGlobalRepositoryOfNodes();
        DomGlobal.console.log(globalRepositoryOfNodes.size() + " = Showing the DRD " + (i == 0 ? "global" : i));

        globalRepositoryOfNodes.forEach((node) -> {

            try {
                if (node.name.equals("ROOT") || i == 0 || node.name.endsWith(i.toString())) {
                    DomGlobal.console.log("KEEPING: ", node.name);
                    add(node);
                } else {
                    DomGlobal.console.log("REMOVING: ", node.name);
                    remove(node);
                }
            } catch (Exception e) {
//                DomGlobal.console.log("ERROR: ", e);
            }
        });
    }

    public void show(final Integer i) {
        final List<DRDNode> globalRepositoryOfNodes = getGlobalRepositoryOfNodes();
        DomGlobal.console.log(globalRepositoryOfNodes.size() + " = Showing the DRD " + (i == 0 ? "global" : i));

        globalRepositoryOfNodes.forEach((node) -> {

            try {
                if (node.name.equals("ROOT") || i == 0 || node.name.endsWith(i.toString())) {
                    DomGlobal.console.log("KEEPING: ", node.name);
                    add(node);
                } else {
                    DomGlobal.console.log("REMOVING: ", node.name);
                    remove(node);
                }
            } catch (Exception e) {
//                DomGlobal.console.log("ERROR: ", e);
            }
        });
    }

    public List<DMNDiagramElement> getDRDs() {
        final Definitions definitions = dmnGraphUtils.getDefinitions();
        return definitions.getDmnDiagramElements();
    }

    public void clear() {
        dmnGraphUtils.getCanvasHandler().clear();
    }

    private void remove(final DRDNode node) {
        final CanvasHandlerImpl canvasHandler = (CanvasHandlerImpl) dmnGraphUtils.getCanvasHandler();
        final AbstractCanvas canvas = canvasHandler.getCanvas();
        final AbstractCanvasView view = (AbstractCanvasView) canvas.getView();

        final SVGShapeViewImpl shapeView = (SVGShapeViewImpl) node.shape.getShapeView();
        shapeView.setAlpha(0);
        view.onResize();
    }

    private void add(final DRDNode node) {
        final CanvasHandlerImpl canvasHandler = (CanvasHandlerImpl) dmnGraphUtils.getCanvasHandler();
        final AbstractCanvas canvas = canvasHandler.getCanvas();
        final AbstractCanvasView view = (AbstractCanvasView) canvas.getView();

        node.shape.getShapeView().setAlpha(1);
        view.onResize();
    }

    String getName(final Element<? extends Definition> element) {
        final TextPropertyProvider provider = textPropertyProviderFactory.getProvider(element);
        return provider.getText(element);
    }

    public List<DRDNode> getGlobalRepositoryOfNodes() {
        if (repositoryOfNodes.isEmpty()) {
            final CanvasHandlerImpl canvasHandler = (CanvasHandlerImpl) dmnGraphUtils.getCanvasHandler();
            final AbstractCanvas canvas = canvasHandler.getCanvas();

            getGraphNodes().forEach(e -> {
                final DRDNode drdNode = new DRDNode();
                drdNode.node = e;
                drdNode.uuid = e.getUUID();
                drdNode.name = getName(e);
                drdNode.content = e.getContent();
                drdNode.shape = canvas.getShape(e.getUUID());
                repositoryOfNodes.add(drdNode);
            });
        }
        return repositoryOfNodes;
    }

    public List<Node> getGraphNodes() {
        return dmnGraphUtils.getNodeStream().collect(Collectors.toList());
    }

    public void show(final DMNDiagramElement drd) {
        final String drdId = drd.getId().getValue();

        dmnGraphUtils.getNodeStream().forEach((Node node) -> {
            final CanvasHandlerImpl canvasHandler = (CanvasHandlerImpl) dmnGraphUtils.getCanvasHandler();
            final AbstractCanvas canvas = canvasHandler.getCanvas();
            final AbstractCanvasView view = (AbstractCanvasView) canvas.getView();

            final Object content = node.getContent();
            if (content instanceof Definition) {
                final Object definition = ((Definition) content).getDefinition();
                if (definition instanceof DRGElement) {

                    final DRGElement drgElement = (DRGElement) definition;
                    final String dmnDiagramId = drgElement.getDMNDiagramId();
                    final Shape shape = canvas.getShape(node.getUUID());

                    final NodeImpl nodeImpl = (NodeImpl) node; // todo if instance of nodeImpl

                    final List<Shape> inEdges = ((List<Edge>) node.getInEdges()).stream().map(e -> canvas.getShape(e.getUUID())).filter(Objects::nonNull).collect(Collectors.toList());
                    final List<Shape> outEdges = ((List<Edge>) node.getOutEdges()).stream().map(e -> canvas.getShape(e.getUUID())).filter(Objects::nonNull).collect(Collectors.toList());

//                    DomGlobal.console.log("-----------------------------------------------------");
//                    inEdges.forEach(e -> DomGlobal.console.log(">> " + e.getUUID()));
//                    outEdges.forEach(e -> DomGlobal.console.log(">> " + e.getUUID()));
//                    DomGlobal.console.log("-----------------------------------------------------");
//                    if (drgElement.getName().getValue().equals("iiiiii")) {
//                        DomGlobal.console.log("iiiiii ===================? " + inEdges.size() + "-" + outEdges.size());
//                    }

                    final SVGShapeViewImpl shapeView = (SVGShapeViewImpl) shape.getShapeView();
//                    DomGlobal.console.log("==> "+ shapeView.getClass().getSimpleName());
                    if (Objects.equals(dmnDiagramId, drdId)) {
//                        DomGlobal.console.log("show " + drgElement.getName().getValue() + "(" + inEdges.size() + ", " + outEdges.size() + ")");
                        shapeView.setAlpha(1);

//                        final WiresContainer container = Js.uncheckedCast(shapeView.getContainer());
//                        container.getChildShapes().add(shapeView);
//                        container.getContainer().add(shapeView.getGroup());

                        inEdges.forEach(e -> e.getShapeView().setAlpha(1));
                        outEdges.forEach(e -> e.getShapeView().setAlpha(1));
                    } else {
//                        DomGlobal.console.log("hide " + drgElement.getName().getValue() + "(" + inEdges.size() + ", " + outEdges.size() + ")");
                        shapeView.setAlpha(0);
//                        shapeView.removeFromParent();

//                        final WiresContainer container = Js.uncheckedCast(shapeView.getContainer());
//                        container.getChildShapes().remove(shapeView);
//                        container.getContainer().remove(shapeView.getGroup());

                        inEdges.forEach(e -> e.getShapeView().setAlpha(0));
                        outEdges.forEach(e -> e.getShapeView().setAlpha(0));
                    }
                }
            }
            refreshCanvas(view);
        });
    }

    private void refreshCanvas(final AbstractCanvasView abstractCanvasView) {
        final WiresCanvasView view = (WiresCanvasView) abstractCanvasView;
        final ScrollableLienzoPanel lienzoPanel = (ScrollableLienzoPanel) view.getLienzoPanel();

        final CanvasPanel panel = view.getPanel();

//        DomGlobal.console.log("-----------------------" + panel.getClass().getSimpleName());

        lienzoPanel.getView().refresh();
        lienzoPanel.refresh();
    }

    public void showAll() {
        dmnGraphUtils.getNodeStream().forEach(node -> {
            final CanvasHandlerImpl canvasHandler = (CanvasHandlerImpl) dmnGraphUtils.getCanvasHandler();
            final AbstractCanvas canvas = canvasHandler.getCanvas();
            final AbstractCanvasView view = (AbstractCanvasView) canvas.getView();

            final Object content = node.getContent();
            if (content instanceof Definition) {
                final Object definition = ((Definition) content).getDefinition();
                if (definition instanceof DRGElement) {

                    final List<Shape> inEdges = ((List<Edge>) node.getInEdges()).stream().map(e -> canvas.getShape(e.getUUID())).filter(Objects::nonNull).collect(Collectors.toList());
                    final List<Shape> outEdges = ((List<Edge>) node.getOutEdges()).stream().map(e -> canvas.getShape(e.getUUID())).filter(Objects::nonNull).collect(Collectors.toList());

                    final Shape shape = canvas.getShape(node.getUUID());
                    shape.getShapeView().setAlpha(1);
                    inEdges.forEach(e -> e.getShapeView().setAlpha(1));
                    outEdges.forEach(e -> e.getShapeView().setAlpha(1));
                }
            }
            view.onResize();
        });
    }

    class DRDNode {

        Object content;
        Node node;
        Shape shape;
        String uuid;
        String name;
    }
}
