package ru.pavlik.chempred.client.widgets;

import com.github.gwtd3.api.Coords;
import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.behaviour.Zoom;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.layout.Force;
import com.github.gwtd3.api.scales.OrdinalScale;
import com.github.gwtd3.api.scales.PowScale;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import ru.pavlik.chempred.client.model.CustomNode;

public class DrawPanelWidget extends FlowPanel implements IsWidget {

    private int width;
    private int height;

    private Selection outer;
    private Selection zoomSelection;
    private Selection vis;
    private Selection dragLine;
    private Force force;
    private Zoom zoom;

    private CustomNode mousdownNode;
    private CustomNode mousupNode;
    private CustomNode selectedNode;
    private Force.Link mousdownLink;
    private Force.Link selectedLink;

    private OrdinalScale color;
    private PowScale radius;

    public DrawPanelWidget() {
    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;

        init();
    }

    Array<CustomNode> nodes = Array.create();
    Array<Force.Link> links = Array.create();

    private void init() {
        color = D3.scale.category20();
        radius = D3.scale.sqrt().range(0, 6);

        zoom = D3.behavior().zoom()
                .on(Zoom.ZoomEventType.ZOOM, rescale());

        outer = D3.select(this)
                .append("svg")
                .attr("width", width)
                .attr("height", height);

        zoomSelection = outer
                .append("svg:g")
                .call(zoom)
                .on("dblclick.zoom", null);

        vis = zoomSelection.append("svg:g")
                .on(BrowserEvents.MOUSEMOVE, mouseMove())
                .on(BrowserEvents.MOUSEDOWN, mouseDown())
                .on(BrowserEvents.MOUSEUP, mouseUp());

        vis.append("svg:rect")
                .attr("width", width)
                .attr("height", height)
                .attr("fill", "white");

        force = D3.layout().force()
                .size(width, height)
                .nodes(Array.fromObjects(CustomNode.create("C", 10)))
                .linkDistance(50)
                .charge(-200);
        force.on(Force.ForceEventType.TICK, tick());

        nodes = force.nodes();
        links = force.links();

        dragLine = vis.append("line")
                .attr("class", "drag_line")
                .attr("x1", 0)
                .attr("y1", 0)
                .attr("x2", 0)
                .attr("y2", 0);

        redraw();
    }

    private DatumFunction<Void> tick() {
        return (context, d, index) -> {
            vis.selectAll(".link")
                    .attr("x1", (context1, d1, index1) -> {
                        return d1.<Force.Link>as().source().x();
                    })
                    .attr("y1", (context1, d1, index1) -> {
                        return d1.<Force.Link>as().source().y();
                    })
                    .attr("x2", (context1, d1, index1) -> {
                        return d1.<Force.Link>as().target().x();
                    })
                    .attr("y2", (context1, d1, index1) -> {
                        return d1.<Force.Link>as().target().y();
                    });

            vis.selectAll(".node")
                    .attr("transform", (context1, d1, index1) -> {
                        CustomNode node = d1.<CustomNode>as();
                        return "translate(" + node.x() + "," + node.y() + ")";
                    });
            return null;
        };
    }

    private DatumFunction<Void> rescale() {
        return (context, d, index) -> {
            Zoom.ZoomEvent zoom = D3.zoomEvent();

            Array<Double> translate = zoom.translate();
            double scale = zoom.scale();

            vis.attr("transform", "translate(" + translate + ")"
                    + " scale(" + scale + ")");
            return null;
        };
    }

    private void resetMouseVars() {
        mousdownNode = null;
        mousupNode = null;
        mousdownLink = null;
    }

    private void redraw() {
        vis.selectAll(".link")
                .data(links).enter()
                .insert("line", ".node")
                .attr("class", "link")
                .on(BrowserEvents.MOUSEDOWN, (context, d, index) -> {
                    mousdownLink = d.<Force.Link>as();
                    if (mousdownLink == selectedLink) {
                        selectedLink = null;
                    } else {
                        selectedLink = mousdownLink;
                    }
                    redraw();
                    return null;
                });

        Selection nodeSelection = vis.selectAll(".node")
                .data(nodes).enter()
                .append("g")
                .attr("class", "node")
                .on(BrowserEvents.MOUSEDOWN, (context, d, index) -> {
                            zoomSelection.on(".zoom", null); //remove zoom

                            mousdownNode = d.<CustomNode>as();
                            if (mousdownNode == selectedNode) {
                                selectedNode = null;
                            } else {
                                selectedNode = mousdownNode;
                            }
                            selectedLink = null;

                            dragLine
                                    .attr("class", "link")
                                    .attr("x1", mousdownNode.x())
                                    .attr("y1", mousdownNode.y())
                                    .attr("x2", mousdownNode.x())
                                    .attr("y2", mousdownNode.y());

                            return null;
                        }
                )
                .on(BrowserEvents.MOUSEUP, (context, d, index) -> {
                    if (mousdownNode != null) {
                        mousupNode = d.<CustomNode>as();
                        if (mousupNode == mousdownNode) {
                            resetMouseVars();
                            return null;
                        }

                        Force.Link link1 = Force.Link.create(mousdownNode, mousupNode);
                        links.push(link1);

                        selectedLink = link1;
                        selectedNode = null;
                        zoomSelection.call(zoom);
                        redraw();
                    }
                    return null;
                });

        nodeSelection.append("circle")
                .attr("r", (context, d, index) -> {
                    CustomNode node = d.<CustomNode>as();
                    return radius.apply(node.getSize()).asDouble();
                })
                .attr("fill", (context, d, index) -> {
                    CustomNode node = d.<CustomNode>as();
                    return color.apply(node.getAtom()).asString();
                });

        nodeSelection.append("text")
                .attr("dy", ".35em")
                .attr("text-anchor", "middle")
                .text((context, d, index) -> {
                    CustomNode node = d.<CustomNode>as();
                    return node.getAtom();
                });

        if (D3.event() != null) {
            // prevent browser's default behavior
            D3.event().preventDefault();
        }

        force.start();
    }

    private DatumFunction<Void> mouseDown() {
        return (context, d, index) -> {
            if (mousdownNode == null && mousdownLink == null) {
                zoomSelection.call(zoom);
            }
            return null;
        };
    }

    private DatumFunction<Void> mouseMove() {
        return (context, d, index) -> {
            if (mousdownNode == null) {
                return null;
            }

            Coords coords = D3.mouseAsCoords(context);
            dragLine
                    .attr("x1", mousdownNode.x())
                    .attr("y1", mousdownNode.y())
                    .attr("x2", coords.x())
                    .attr("y2", coords.y());

            force.stop();
            return null;
        };
    }

    private DatumFunction<Void> mouseUp() {
        return (context, d, index) -> {
            if (mousdownNode != null) {
                // hide drag line
                dragLine.attr("class", "drag_line_hidden");

                if (mousupNode == null) {
                    Coords coords = D3.mouseAsCoords(context);
                    CustomNode newNode = CustomNode.create("C", 10, coords.x(), coords.y());

                    nodes.push(newNode);
                    selectedNode = newNode;
                    selectedLink = null;

                    links.push(Force.Link.create(mousdownNode, newNode));
                }

                redraw();
            }
            // clear mouse event vars
            resetMouseVars();
            return null;
        };
    }
}