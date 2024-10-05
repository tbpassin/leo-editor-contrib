<?xml version="1.0" encoding="utf-8"?>
<!-- Created by Leo: https://leo-editor.github.io/leo-editor/leo_toc.html -->
<leo_file xmlns:leo="https://leo-editor.github.io/leo-editor/namespaces/leo-python-editor/1.1" >
<leo_header file_format="2"/>
<globals/>
<preferences/>
<find_panel_settings/>
<vnodes>
<v t="tom.20240921075238.1"><vh>@settings</vh>
<v t="tom.20240921075317.1"><vh>Implementation 2 (production)</vh>
<v t="tom.20240930232554.1"><vh>Layout Commands</vh>
<v t="tom.20240930232554.5"><vh>@command layout-make-tabbed-body-pane</vh></v>
<v t="tom.20240930232554.6"><vh>@command layout-vr3-to-tabbed-body</vh></v>
<v t="tom.20240930232554.7"><vh>@command layout-vr-to-tabbed-body</vh></v>
<v t="tom.20240930232554.8"><vh>@command layout-toggle-vr-in-split-main</vh></v>
<v t="tom.20240930232554.9"><vh>@command layout-toggle-vr3-in-log</vh></v>
<v t="tom.20240930232554.10"><vh>@command layout-open-rpcalc-in-tabbed-body</vh></v>
</v>
<v t="tom.20240921080119.1"><vh>Menus</vh>
<v t="tom.20240921080140.1"><vh>@menuat help before</vh>
<v t="tom.20240930232952.1"><vh>@menu Layout &amp;Demo</vh>
<v t="tom.20240930232952.2"><vh>@item layout-restore-default</vh></v>
<v t="tom.20240930232952.3"><vh>@item layout-fallback-layout</vh></v>
<v t="tom.20240930232952.4"><vh>@item layout-legacy</vh></v>
<v t="tom.20240930232952.5"><vh>@item vr3-toggle</vh></v>
<v t="tom.20240930232952.6"><vh>@item vr-toggle</vh></v>
<v t="tom.20240930232952.7"><vh>@menu Built-in Layouts</vh>
<v t="tom.20240930232952.8"><vh>@item layout-vertical-thirds</vh></v>
<v t="tom.20240930232952.9"><vh>@item layout-vertical-thirds2</vh></v>
<v t="tom.20240930232952.10"><vh>@item layout-big-tree</vh></v>
<v t="tom.20240930232952.11"><vh>@item layout-horizontal-thirds</vh></v>
<v t="tom.20240930232952.12"><vh>@item layout-render-focused</vh></v>
</v>
<v t="tom.20240930232952.13"><vh>@item -</vh></v>
<v t="tom.20240930232952.14"><vh>@item layout-swap-log-panel</vh></v>
<v t="tom.20240930232952.15"><vh>@item layout-make-tabbed-body-pane</vh></v>
<v t="tom.20240930232952.16"><vh>@menu &amp;VR3 Layouts</vh>
<v t="tom.20240930232952.17"><vh>@item layout-vr3-to-tabbed-body</vh></v>
<v t="tom.20240930232952.19"><vh>@item layout-toggle-vr3-in-log</vh></v>
</v>
<v t="tom.20240930232952.20"><vh>@menu VR Layouts</vh>
<v t="tom.20240930232952.21"><vh>@item layout-vr-to-tabbed-body</vh></v>
<v t="tom.20240930232952.22"><vh>@item layout-toggle-vr-in-split-main</vh></v>
</v>
<v t="tom.20240930232952.23"><vh>@menu Other Layouts</vh>
<v t="tom.20240930232952.24"><vh>@item layout-quadrant</vh></v>
</v>
<v t="tom.20240930232952.25"><vh>@item -</vh></v>
<v t="tom.20240930232952.26"><vh>@item layout-open-rpcalc-in-tabbed-body</vh></v>
</v>
</v>
</v>
</v>
</v>
<v t="tom.20240902135000.1"><vh>About This Outline</vh></v>
</vnodes>
<tnodes>
<t tx="tom.20240902135000.1">@nocolor
This outline contains the prototype of a script-based layout system to arrange Leo's various panes. The system was proposed in https://groups.google.com/g/leo-editor/c/EYQ4odwLLxY

Many of the menu items dispatch commands that depend on new code contained in the "tbp-leo_layouts branch that is currently in my clone of leo-editor repo at:

    https://github.com/tbpassin/leo-editor/tree/tbp-leo_layouts

The outline will add a new menu "Layout Demo" to Leo's menus for the outline.

Most of the machinery for the layout system is contained in the new file leo/plugins/qt_layout.py. The various commands there show how to script new layouts. A number of layout description data structures and layout commands are also included in the qt_layout module. This module is not loaded by Leo's plugin manager even though it is located in the plugins directory.

Key Design Features
--------------------
The key feature is a new class named "LayoutCacheWidget".  This widget serves as a place to store, or cache, unused splitters and other widgets, such as VR and VR3, when they are not included in a layout.  Since they will be in a known location, they can be found when needed by a search method such as g.app.gui.find_widget_by_name().  The class also contains code that can apply a layout from a simple layout description data structure.

The DynamicWindow of an outline contains an instance of this widget. This instance can be referenced by this code snippet::

    dw = c.frame.top
    cache = dw.layout_cache

Given a layout name, the layout can be applied this way::

    cache.restoreFromLayout(layout_name)

If the layout refers to a splitter that does not yet exist, it is created. The outline contains layouts for most of the layouts that are currently built in to Leo, and a few more besides.

Applying a layout consists of these steps:

#. Create any new splitters that are needed. Leo's "main_splitter" and "secondary_splitter" always exist.

#. Move any other splitters and widgets that may have been added previously to the cache using widget.setParent(cache).

#. Look for widgets that should be in the new layout using cache.find_widget() oy g.app.gui.find_widget_by_name().

#. Insert widgets into their target splitters using splitter.addWidget() or splitter.insertWidget().

#. Insert splitters into their intended containing splitters.

#. Set the orientation of each splitter (vertical or horizontal).

#. Equalize the size of each splitter, ending with the main_splitter.

These steps can be done by a script, or cache.restoreFromLayout() will do them all according to the layout data structure.</t>
<t tx="tom.20240921075238.1"></t>
<t tx="tom.20240921075317.1"></t>
<t tx="tom.20240921080119.1"></t>
<t tx="tom.20240921080140.1"></t>
<t tx="tom.20240930232554.1"></t>
<t tx="tom.20240930232554.10">@language python
"""Add RPCalc widget to tabbed body

Only works if the body is already tabbed.
Otherwise RPCalc will open in a new tab in the
Log frame.
"""

from leo.core.leoQt import Qt
from leo.plugins import rpcalc

def find_widget(name):
    return g.app.gui.find_widget_by_name(c, name)

tw = find_widget('bodyTabWidget')
if tw:
    rcalc = find_widget('rpcalc')
    if not rcalc:
        rcalc = rpcalc.CalcDlg()
        rcalc.setObjectName('rpcalc')

    if tw.indexOf(rcalc) == -1:
        index = tw.addTab(rcalc, 'RPCalc')
        tw.setCurrentIndex(index)
        # Avoid keypress on both key-up and key-down
        rcalc.standalone = True
        rcalc.setFocus(Qt.FocusReason.OtherFocusReason)
else:
    c.doCommandByName('rpcalc-toggle')
</t>
<t tx="tom.20240930232554.5">@language python
from leo.core.leoQt import QtWidgets

def find_widget(name):
    return g.app.gui.find_widget_by_name(c, name)

# Find splitters, etc.
ms = find_widget('main_splitter')
bp2 = find_widget('bodyPage2')
sw = find_widget('bodyStackedWidget')

# Find or make singleton bodyTabWidget
tw = find_widget('bodyTabWidget')
if not tw:
    tw = QtWidgets.QTabWidget(None)
    tw.setObjectName('bodyTabWidget')

tw.insertTab(0, bp2, 'Body Editor')
tw.setCurrentIndex(0)
tw.setMovable(True)
sw.insertWidget(0, tw)
sw.setCurrentIndex(0)
</t>
<t tx="tom.20240930232554.6">@language python
"""Add widget to tabbed body.

Only works if the body is already tabbed.
Otherwise VR3 will open in a new tab in the
Log frame.
"""

CACHENAME = 'leo-layout-cache'

def find_widget(name):
    return g.app.gui.find_widget_by_name(c, name)

vr3 = find_widget('viewrendered3_pane')
if not vr3:
    import leo.plugins.viewrendered3 as v3
    try:
        vr3 = v3.getVr3({'c':c})
    except:
        h = c.hash()
        vr3 = v3.controllers[h] = v3.ViewRenderedController3(c)

tw = find_widget('bodyTabWidget')
if tw and tw.parent().objectName() != CACHENAME:
        index = tw.addTab(vr3, 'VR3')
        tw.setCurrentIndex(index)
else:
    c.doCommandByName('vr3-tab')

</t>
<t tx="tom.20240930232554.7">@language python
"""Add widget to body frame tabbed widget.

Only works if the bodyStackedWidget has been changed to or
contains a QTabWidget. Otherwise VR will open in its default position.
"""

def find_widget(name):
    return g.app.gui.find_widget_by_name(c, name)

CACHENAME = 'leo-layout-cache'

# Find or create VR widget
vr = find_widget('viewrendered_pane')
if not vr:
    import leo.plugins.viewrendered as v
    # Alternate versions for earlier revisions of VR
    try:
        vr = v.getVr()
    except:
        h = c.hash()
        vr.controllers[h] = vr = v.ViewRenderedController(c)

tw = find_widget('bodyTabWidget')
if tw and tw.parent().objectName() != CACHENAME:
    if tw.indexOf(vr) == -1:
        index = tw.addTab(vr, 'VR')
        tw.setCurrentIndex(index)
    c.doCommandByName('vr-show')
else:
    c.doCommandByName('vr-show')
</t>
<t tx="tom.20240930232554.8">@language python
"""Toggle VR in main splitter without destroying the instance."""
from leo.core.leoQt import QtCore

def find_widget(name):
    return g.app.gui.find_widget_by_name(c, name)

def show_pane(w):
    w.setUpdatesEnabled(True)
    c.doCommandByName('vr-show')

cache = find_widget('leo-layout-cache')

# Find or create VR widget
vr = find_widget('viewrendered_pane')
if not vr:
    import leo.plugins.viewrendered as v
    # Alternate versions for earlier revisions of VR
    try:
        vr = v.getVr()
    except:
        h = c.hash()
        vr.controllers[h] = vr = v.ViewRenderedController(c)

ms = find_widget('main_splitter')

if vr.parent() == ms:
    vr.setParent(cache)
else:
    vr.setParent(ms)
    g.app.gui.equalize_splitter(ms)

    # Avoid flash each time VR pane is re-opened.
    QtCore.QTimer.singleShot(60, lambda: show_pane(vr))
</t>
<t tx="tom.20240930232554.9">TABNAME = 'VR3'
CACHENAME = 'leo-layout-cache'

log = c.frame.log

vr3 = g.app.gui.find_widget_by_name(c, 'viewrendered3_pane')
if not vr3:
    import leo.plugins.viewrendered3 as v3
    vr3 = v3.getVr3({'c':c})

# if a VR3 tab exists, remove the vr3 widget
index = log.findTabIndex(TABNAME)
if index is not None:
    cache = g.app.gui.find_widget_by_name(c, CACHENAME)
    vr3.setParent(cache)
else:
    log.createTab(TABNAME, widget = vr3, createText = False)
    log.selectTab(TABNAME)

</t>
<t tx="tom.20240930232952.1"></t>
<t tx="tom.20240930232952.10">&amp;Big Tree Layout (With VR)
</t>
<t tx="tom.20240930232952.11">&amp;Horizontal Thirds (With VR3)</t>
<t tx="tom.20240930232952.12">&amp;Render-focused Layout (With VR)</t>
<t tx="tom.20240930232952.13"></t>
<t tx="tom.20240930232952.14">&amp;Swap Log Frame Location
</t>
<t tx="tom.20240930232952.15">Make &amp;Body Tabbed</t>
<t tx="tom.20240930232952.16"></t>
<t tx="tom.20240930232952.17">&amp;VR3 - Open In Tabbed Body</t>
<t tx="tom.20240930232952.19">VR3 - Toggle In &amp;Log Frame</t>
<t tx="tom.20240930232952.2">&amp;Restore Default Layout From Settings</t>
<t tx="tom.20240930232952.20"></t>
<t tx="tom.20240930232952.21">VR - &amp;Open In Tabbed Body</t>
<t tx="tom.20240930232952.22">VR -Toggle In Split &amp;Main Panel</t>
<t tx="tom.20240930232952.23"></t>
<t tx="tom.20240930232952.24">&amp;Quadrant Layout</t>
<t tx="tom.20240930232952.25"></t>
<t tx="tom.20240930232952.26">RPCalc - Open In Tabbed Body</t>
<t tx="tom.20240930232952.3">The &amp;Fallback Layout</t>
<t tx="tom.20240930232952.4">&amp;Leo's "Legacy" Layout</t>
<t tx="tom.20240930232952.5">&amp;Toggle VR3</t>
<t tx="tom.20240930232952.6">&amp;Toggle VR</t>
<t tx="tom.20240930232952.7"></t>
<t tx="tom.20240930232952.8">&amp;Vertical-thirds Layout (With VR)</t>
<t tx="tom.20240930232952.9">&amp;Vertical-thirds Layout #2 (With VR)</t>
</tnodes>
</leo_file>
