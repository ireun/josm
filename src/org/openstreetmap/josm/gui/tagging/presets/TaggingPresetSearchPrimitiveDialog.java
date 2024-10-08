// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui.tagging.presets;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmData;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.Shortcut;

/**
 * A dialog that allows to select a preset and then selects all matching OSM objects.
 * @see org.openstreetmap.josm.gui.tagging.presets.TaggingPresetSearchDialog
 */
public final class TaggingPresetSearchPrimitiveDialog extends ExtendedDialog {

    private static TaggingPresetSearchPrimitiveDialog instance;

    private final TaggingPresetSelector selector;

    /**
     * An action executing {@link TaggingPresetSearchPrimitiveDialog}.
     */
    public static class Action extends JosmAction {

        /**
         * Constructs a new {@link TaggingPresetSearchPrimitiveDialog.Action}.
         */
        public Action() {
            super(tr("Search for objects by preset..."), "dialogs/search", tr("Search for objects by their presets."),
                    Shortcut.registerShortcut("preset:search-objects", tr("Presets: {0}", tr("Search for objects by preset...")),
                    KeyEvent.VK_F3, Shortcut.SHIFT), true, "presets/search-objects", true);
            setHelpId(ht("/Action/TaggingPresetSearchPrimitive"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (MainApplication.getLayerManager().getActiveData() != null) {
                TaggingPresetSearchPrimitiveDialog.getInstance().showDialog();
            }
        }

        @Override
        protected void updateEnabledState() {
            setEnabled(getLayerManager().getActiveData() != null);
        }
    }

    /**
     * Returns the unique instance of {@code TaggingPresetSearchPrimitiveDialog}.
     * @return the unique instance of {@code TaggingPresetSearchPrimitiveDialog}.
     */
    public static synchronized TaggingPresetSearchPrimitiveDialog getInstance() {
        if (instance == null) {
            instance = new TaggingPresetSearchPrimitiveDialog();
        }
        return instance;
    }

    TaggingPresetSearchPrimitiveDialog() {
        super(MainApplication.getMainFrame(), tr("Search for objects by preset"), tr("Search"), tr("Cancel"));
        setButtonIcons("dialogs/search", "cancel");
        configureContextsensitiveHelp("/Action/TaggingPresetSearchPrimitive", true /* show help button */);
        selector = new TaggingPresetSelector(false, false);
        setContent(selector, false);
        selector.setDblClickListener(e -> buttonAction(0, null));
    }

    @Override
    public ExtendedDialog showDialog() {
        selector.init();
        super.showDialog();
        selector.clearSelection();
        return this;
    }

    @Override
    protected void buttonAction(int buttonIndex, ActionEvent evt) {
        super.buttonAction(buttonIndex, evt);
        if (buttonIndex == 0) {
            TaggingPreset preset = selector.getSelectedPresetAndUpdateClassification();
            if (preset != null) {
                OsmData<?, ?, ?, ?> ds = MainApplication.getLayerManager().getActiveData();
                ds.setSelected(new HashSet<>(ds.getPrimitives(preset)));
            }
        }
    }
}
