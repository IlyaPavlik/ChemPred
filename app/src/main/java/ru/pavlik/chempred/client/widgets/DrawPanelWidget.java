package ru.pavlik.chempred.client.widgets;

import com.github.gwtd3.api.Coords;
import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.arrays.ForEachCallback;
import com.github.gwtd3.api.behaviour.Zoom;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.UpdateSelection;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.layout.Force;
import com.github.gwtd3.api.scales.OrdinalScale;
import com.github.gwtd3.api.scales.PowScale;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;
import ru.pavlik.chempred.client.model.LinkType;
import ru.pavlik.chempred.client.model.js.ElementLink;
import ru.pavlik.chempred.client.model.js.ElementNode;
import ru.pavlik.chempred.client.model.js.Structure;
import ru.pavlik.chempred.client.utils.AppBundle;

public class DrawPanelWidget extends FlowPanel implements IsWidget {

    private static final int ELEMENT_RADIUS = 10;

    private int width;
    private int height;

    private Selection outer;
    private Selection zoomSelection;
    private Selection vis;
    private Selection dragLine;
    private Selection dragPath;
    private Force force;
    private Zoom zoom;

    private ElementNode mousdownNode;
    private ElementNode mousupNode;
    private ElementNode selectedNode;
    private Force.Link mousdownLink;
    private Force.Link selectedLink;

    private Array<ElementNode> nodes = Array.create();
    private Array<ElementLink> links = Array.create();

    private OrdinalScale color;
    private PowScale radius;
    private ElementNode currentElement = null;
    private LinkType linkType = LinkType.SINGLE;

    public DrawPanelWidget() {
        AppBundle.INSTANCE.elementsCss().ensureInjected();
    }

    public void setCurrentElement(ElementNode currentElement) {
        this.currentElement = currentElement;
        this.currentElement.setSize(ELEMENT_RADIUS);
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public void setStructure(Structure structure) {
        if (force != null) {
            Array<ElementLink> linkArray = Array.create();
            for (ElementLink link : structure.getElementLinks()) {
                linkArray.push(link);
            }
            this.links = linkArray;
            force.links(linkArray);

            Array<ElementNode> nodeArray = Array.create();
            for (ElementNode node : structure.getElementNodes()) {
                nodeArray.push(node);
            }

            this.nodes = nodeArray;
            force.nodes(nodeArray);

            redraw();
        }
    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;

        init();
    }

    public void clear() {
        nodes = Array.create();
        links = Array.create();
        force.nodes(nodes);
        force.links(links);

        redraw();
    }

    public Structure getStructure() {
        Structure structure = new Structure();
        structure.setElementLinks(links.asList());
        structure.setElementNodes(nodes.asList());
        return structure;
    }

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

        //define pattern for Down link
        vis.append("defs")
                .append("pattern")
                .attr("id", "mask-stripe")
                .attr("patternUnits", "userSpaceOnUse")
                .attr("width", "5")
                .attr("height", "5")
                .append("rect")
                .attr("width", "2")
                .attr("height", "12")
                .attr("fill", "black");

        force = D3.layout().force()
                .size(width, height)
                .linkDistance(80)
                .charge(-400);
        force.on(Force.ForceEventType.TICK, tick());

        nodes = force.nodes();
        links = force.links();

        dragLine = vis.append("line")
                .attr("class", "drag_line")
                .attr("x1", 0)
                .attr("y1", 0)
                .attr("x2", 0)
                .attr("y2", 0);

        dragPath = vis.append("path")
                .attr("class", "link_triangle");

        D3.select("body")
                .on(BrowserEvents.KEYDOWN, (context, d, index) -> {
                    if (selectedNode == null) {
                        return null;
                    }

                    switch (D3.event().getKeyCode()) {
                        case 8:// backspace
                        case 46:// delete
                            nodes.splice(nodes.indexOf(selectedNode), 1);
                            spliceLinksForNodes(selectedNode);
                            redraw();
                            break;
                    }
                    return null;
                });

        redraw();
    }

    private DatumFunction<Void> tick() {
        return (context, d, index) -> {
            //draw simple links
            vis.selectAll(".link_container")
                    .selectAll("line")
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

            //draw triangle links
            vis.selectAll(".link_container")
                    .selectAll("path")
                    .attr("d", (context1, d1, index1) -> {
                        ElementLink elementLink = d1.<ElementLink>as();
                        double x = elementLink.target().x() - elementLink.source().x();
                        double y = elementLink.target().y() - elementLink.source().y();
                        return getTrianglePath(x, y);
                    })
                    .attr("transform", (context1, d1, index1) -> {
                        ElementLink elementLink = d1.<ElementLink>as();
                        double x = elementLink.target().x() - elementLink.source().x();
                        double y = elementLink.target().y() - elementLink.source().y();
                        return getTriangleTransform(elementLink.source().x(), elementLink.source().y(), x, y);
                    });

            //draw nodes
            vis.selectAll(".node")
                    .attr("transform", (context1, d1, index1) -> {
                        ElementNode node = d1.<ElementNode>as();
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
        drawLinks();

        UpdateSelection dataSelection = vis.selectAll(".node").data(nodes);
        dataSelection.enter().append("g")
                .attr("class", "node")
                .on(BrowserEvents.MOUSEDOWN, (context, d, index) -> {
                            zoomSelection.on(".zoom", null); //remove zoom
                            mousdownNode = d.<ElementNode>as();

                            if (mousdownNode.getValence() - linkType.getWeight() < 0) {
                                mousdownNode = null;
                                return null;
                            }
                            selectedLink = null;

                            dragLine
                                    .attr("class", "drag_line")
                                    .attr("x1", mousdownNode.x())
                                    .attr("y1", mousdownNode.y())
                                    .attr("x2", mousdownNode.x())
                                    .attr("y2", mousdownNode.y());

                            return null;
                        }
                )
                .on(BrowserEvents.MOUSEUP, (context, d, index) -> {
                    if (mousdownNode != null) {
                        mousupNode = d.<ElementNode>as();
                        if (mousupNode == mousdownNode) {
                            resetMouseVars();
                            return null;
                        }

                        mousdownNode.setValence(mousdownNode.getValence() - linkType.getWeight());
                        mousupNode.setValence(mousupNode.getValence() - linkType.getWeight());

                        ElementLink elementLink = ElementLink.create(mousdownNode, mousupNode, linkType);
                        links.push(elementLink);

                        selectedLink = elementLink;
                        zoomSelection.call(zoom);
                        redraw();
                    }
                    return null;
                })
                .on(BrowserEvents.MOUSEOVER, (context, d, index) -> {
                    selectedNode = d.<ElementNode>as();
                    Element circle = context.getFirstChildElement();
                    circle.setAttribute("stroke-width", "3px");
                    return null;
                })
                .on(BrowserEvents.MOUSEOUT, (context, d, index) -> {
                    selectedNode = null;
                    Element circle = context.getFirstChildElement();
                    circle.setAttribute("stroke-width", "1.5px");
                    return null;
                });

        dataSelection.append("circle")
                .attr("r", (context, d, index) -> {
                    ElementNode node = d.<ElementNode>as();
                    return radius.apply(node.getSize()).asDouble();
                })
                .attr("fill", (context, d, index) -> {
                    ElementNode node = d.<ElementNode>as();
                    return color.apply(node.getAtom()).asString();
                });
        dataSelection.append("text")
                .text("")
                .attr("dy", ".35em")
                .attr("text-anchor", "middle")
                .append("tspan")
                .text((context, d, index) -> {
                    ElementNode node = d.<ElementNode>as();
                    String atom = node.getAtom();
                    if (node.getValence() > 0) {
                        atom += "H";
                    }
                    return atom;
                })
                .append("tspan")
                .attr("baseline-shift", "sub")
                .text((context, d, index) -> {
                    ElementNode node = d.<ElementNode>as();
                    if (node.getValence() > 1) {
                        return String.valueOf(node.getValence());
                    }
                    return "";
                });
        dataSelection.exit().remove();

        if (D3.event() != null) {
            // prevent browser's default behavior
            D3.event().preventDefault();
        }

        force.start();
    }

    private void drawLinks() {
        UpdateSelection linkDataSelection = vis.selectAll(".link_container").data(links);
        Selection linkSelections = linkDataSelection.enter()
                .insert("g", ".node")
                .attr("class", "link_container");

        //Draw single links
        linkSelections.filter((context, d, index) -> {
            ElementLink elementLink = d.<ElementLink>as();
            return elementLink.getType() == LinkType.SINGLE ? context : null;
        })
                .append("line")
                .attr("class", "link")
                .style("stroke-width", (context, d, index) -> {
                    ElementLink elementLink = d.<ElementLink>as();
                    return (elementLink.getType().getWeight() * 2 - 1) * 2 + "px";
                });

        //Draw double links
        linkSelections.filter((context, d, index) -> {
            ElementLink elementLink = d.<ElementLink>as();
            return elementLink.getType() == LinkType.DOUBLE ? context : null;
        })
                .append("line")
                .attr("class", "link_double_separator");

        //Draw triple links
        Selection tripleSelection = linkSelections.filter((context, d, index) -> {
            ElementLink elementLink = d.<ElementLink>as();
            return elementLink.getType() == LinkType.TRIPLE ? context : null;
        });
        tripleSelection.append("line")
                .attr("class", "link_triple_separator");
        tripleSelection.append("line")
                .attr("class", "link");

        //Draw triangle links
        linkSelections
                .filter((context, d, index) -> {
                    ElementLink elementLink = d.<ElementLink>as();
                    return elementLink.getType() == LinkType.TOP ? context : null;
                })
                .append("path")
                .style("fill", "black")
                .attr("d", (context, d, index) -> {
                    ElementLink elementLink = d.<ElementLink>as();
                    double x = elementLink.target().x() - elementLink.source().x();
                    double y = elementLink.target().y() - elementLink.source().y();
                    return getTrianglePath(x, y);
                })
                .attr("transform", (context, d, index) -> {
                    ElementLink elementLink = d.<ElementLink>as();
                    double x = elementLink.target().x() - elementLink.source().x();
                    double y = elementLink.target().y() - elementLink.source().y();
                    return getTriangleTransform(elementLink.source().x(), elementLink.source().y(), x, y);
                });

        linkSelections
                .filter((context, d, index) -> {
                    ElementLink elementLink = d.<ElementLink>as();
                    return elementLink.getType() == LinkType.DOWN ? context : null;
                })
                .append("path")
                .style("fill", "url(#mask-stripe)")
                .attr("d", (context, d, index) -> {
                    ElementLink elementLink = d.<ElementLink>as();
                    double x = elementLink.target().x() - elementLink.source().x();
                    double y = elementLink.target().y() - elementLink.source().y();
                    return getTrianglePath(x, y);
                })
                .attr("transform", (context, d, index) -> {
                    ElementLink elementLink = d.<ElementLink>as();
                    double x = elementLink.target().x() - elementLink.source().x();
                    double y = elementLink.target().y() - elementLink.source().y();
                    return getTriangleTransform(elementLink.source().x(), elementLink.source().y(), x, y);
                });
        ;

        linkDataSelection.exit().remove();
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

            if (linkType == LinkType.TOP || linkType == LinkType.DOWN) {
                double x = coords.x() - mousdownNode.x();
                double y = coords.y() - mousdownNode.y();

                dragPath
                        .attr("fill", (context1, d1, index1) ->
                                linkType == LinkType.TOP ? "black" : "url(#mask-stripe)")
                        .attr("d", getTrianglePath(x, y))
                        .attr("transform", getTriangleTransform(mousdownNode.x(), mousdownNode.y(), x, y));
            } else {
                dragLine
                        .attr("x1", mousdownNode.x())
                        .attr("y1", mousdownNode.y())
                        .attr("x2", coords.x())
                        .attr("y2", coords.y());
            }

            force.stop();
            return null;
        };
    }

    private DatumFunction<Void> mouseUp() {
        return (context, d, index) -> {
            if (mousdownNode != null) {
                // hide drag line
                dragLine.attr("class", "drag_line_hidden");
                dragPath.attr("class", "link_triangle_hidden").attr("d", "");

                if (mousupNode == null) {
                    Coords coords = D3.mouseAsCoords(context);
                    ElementNode newNode = ElementNode.create(coords.x(), coords.y(), currentElement);

                    nodes.push(newNode);
                    selectedLink = null;

                    mousdownNode.setValence(mousdownNode.getValence() - linkType.getWeight());
                    newNode.setValence(newNode.getValence() - linkType.getWeight());

                    links.push(ElementLink.create(mousdownNode, newNode, linkType));
                }
                redraw();
            } else if (nodes.length() == 0 && currentElement != null) {
                Coords coords = D3.mouseAsCoords(context);
                nodes.push(ElementNode.create(coords.x(), coords.y(), currentElement));
                redraw();
            } else if (currentElement == null) {
                //TODO refactor, move to utils class
                NotifySettings settings = NotifySettings.newSettings();
                settings.setDelay(2000);
                Notify.notify("Выберите химический элемент", settings);
            }
            // clear mouse event vars
            resetMouseVars();
            return null;
        };
    }

    private void spliceLinksForNodes(Force.Node elementNode) {
        Array toSplice = links.filter((thisArg, element, index, array) -> {
            ElementLink elementLink = element.as();
            return elementLink.target() == elementNode || elementLink.source() == elementNode;
        });
        toSplice.map((ForEachCallback<Void>) (thisArg, element, index, array) -> {
            ElementLink elementLink = element.as();
            links.splice(links.indexOf(elementLink), 1);
            return null;
        });
    }

    private String getTrianglePath(double x, double y) {
        double dist = Math.sqrt(x * x + y * y);

        return "M0,0 L" +
                0 + "," + ELEMENT_RADIUS + "," +
                (dist - ELEMENT_RADIUS) + "," + 0 + "," +
                dist + "," + 0 + "," +
                (dist - ELEMENT_RADIUS) + "," + 0 + "," +
                0 + "," + (-ELEMENT_RADIUS) +
                "z";
    }

    private String getTriangleTransform(double sourceX, double sourceY, double x, double y) {
        double angle = Math.atan2(y, x) / Math.PI * 180;
        return "translate(" + sourceX + "," + sourceY + ") rotate(" + angle + ")";
    }
}
