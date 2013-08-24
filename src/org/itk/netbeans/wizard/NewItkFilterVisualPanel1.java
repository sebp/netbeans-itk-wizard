/*
 * Netbeans plugin to create ITK image filters.
 * Copyright (C) 2013  Sebastian Pölsterl <sebp@k-d-w.org>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.itk.netbeans.wizard;

import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

public final class NewItkFilterVisualPanel1 extends JPanel {

    private static final String sourceExt = "hxx"; // NOI18N
    private static final String headerExt = "h"; // NOI18N
    private Project project;
    private SourceGroup[] folders;

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * Creates new form ItkFilterVisualPanel1
     */
    public NewItkFilterVisualPanel1(Project project, SourceGroup[] folders) {
        this.project = project;
        this.folders = folders;

        initComponents();
        initValues(null, null, null);
    }

    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    @Override
    public String getName() {
        return "Step #1";
    }

    public String getClassName() {
        return classNameTextField.getText().trim();
    }

    public String getParentClassName() {
        return (String) parentClassComboBox.getSelectedItem();
    }

    public void setParentClassName(String parent) {
        parentClassComboBox.setSelectedItem(parent);
    }

    public boolean isMultiThreaded() {
        return multiThreadedCheckBox.isSelected();
    }

    public void setMultiThreaded(Boolean value) {
        if (value != null) {
            multiThreadedCheckBox.setSelected(value);
        }
    }

    public void initValues(FileObject template, FileObject preselectedFolder, String documentName) {
        assert project != null;

        Sources sources = ProjectUtils.getSources(project);

        folders = sources.getSourceGroups(Sources.TYPE_GENERIC);

        if (folders.length < 2) {
            // one source group i.e. hide Location
            locationLabel.setVisible(false);
            locationComboBox.setVisible(false);
        } else {
            // more source groups user needs to select location
            locationLabel.setVisible(true);
            locationComboBox.setVisible(true);
        }

        parentClassComboBox.setSelectedIndex(0);
        locationComboBox.setModel(new DefaultComboBoxModel(folders));
        // Guess the group we want to create the file in
        SourceGroup preselectedGroup = getPreselectedGroup(folders, preselectedFolder);
        locationComboBox.setSelectedItem(preselectedGroup);
        // Create OS dependent relative name
        String relPreselectedFolder = getRelativeNativeName(preselectedGroup.getRootFolder(), preselectedFolder);
        folderTextField.setText(relPreselectedFolder);

        String displayName = null;
        try {
            if (template != null) {
                DataObject templateDo = DataObject.find(template);
                displayName = templateDo.getNodeDelegate().getDisplayName();
            }
        } catch (DataObjectNotFoundException ex) {
            displayName = template.getName();
        }
        putClientProperty("NewFileWizard_Title", displayName);// NOI18N

        if (template != null) {
            if (documentName == null) {
                final String baseName = getMessage("NewClassSuggestedName");
                documentName = baseName;
                FileObject currentFolder = preselectedFolder != null ? preselectedFolder : getTargetGroup().getRootFolder();
                if (currentFolder != null) {
                    documentName += generateUniqueSuffix(
                            currentFolder, documentName,
                            sourceExt, headerExt);
                }

            }
            classNameTextField.setText(documentName);
            classNameTextField.selectAll();
        }

    }

    public SourceGroup getTargetGroup() {
        Object selectedItem = locationComboBox.getSelectedItem();
        if (selectedItem == null) {
            // workaround for MacOS, see IZ 175457
            selectedItem = locationComboBox.getItemAt(locationComboBox.getSelectedIndex());
            if (selectedItem == null) {
                selectedItem = locationComboBox.getItemAt(0);
            }
        }
        return (SourceGroup) selectedItem;
    }

    public String getTargetFolder() {
        String folderName = folderTextField.getText().trim();

        if (folderName.length() == 0) {
            return "";
        } else {
            return folderName.replace(File.separatorChar, '/'); // NOI18N
        }
    }

    protected static String getRelativeNativeName(FileObject root, FileObject folder) {
        if (root == null) {
            throw new NullPointerException("null root passed to getRelativeNativeName"); // NOI18N
        }

        String path;

        if (folder == null) {
            path = ""; // NOI18N
        } else {
            path = FileUtil.getRelativePath(root, folder);
        }

        return path == null ? "" : path.replace('/', File.separatorChar); // NOI18N
    }

    protected static SourceGroup getPreselectedGroup(SourceGroup[] groups, FileObject folder) {
        for (int i = 0; folder != null && i < groups.length; i++) {
            if (FileUtil.isParentOf(groups[i].getRootFolder(), folder)
                    || groups[i].getRootFolder().equals(folder)) {
                return groups[i];
            }
        }
        return groups[0];
    }

    protected static String generateUniqueSuffix(FileObject folder, String prefix, String... extensions) {
        for (int i = 0; true; ++i) {
            String suffix = i == 0 ? "" : String.valueOf(i);
            String filename = prefix + suffix;
            boolean unique = true;
            for (String ext : extensions) {
                if (folder.getFileObject(filename, ext) != null) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                return suffix;
            }
        }
    }

    protected static String getMessage(String name) {
        return NbBundle.getMessage(NewItkFilterVisualPanel1.class, name);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        parentClassComboBox = new javax.swing.JComboBox();
        multiThreadedCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        classNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        folderTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        locationLabel = new javax.swing.JLabel();
        locationComboBox = new javax.swing.JComboBox();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(NewItkFilterVisualPanel1.class, "NewItkFilterVisualPanel1.jLabel2.text")); // NOI18N

        parentClassComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ImageToImageFilter", "InPlaceImageFilter" }));
        parentClassComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentClassComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(multiThreadedCheckBox, org.openide.util.NbBundle.getMessage(NewItkFilterVisualPanel1.class, "NewItkFilterVisualPanel1.multiThreadedCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(NewItkFilterVisualPanel1.class, "NewItkFilterVisualPanel1.jLabel1.text")); // NOI18N

        classNameTextField.setText(org.openide.util.NbBundle.getMessage(NewItkFilterVisualPanel1.class, "NewItkFilterVisualPanel1.classNameTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(NewItkFilterVisualPanel1.class, "NewItkFilterVisualPanel1.jLabel3.text")); // NOI18N

        folderTextField.setText(org.openide.util.NbBundle.getMessage(NewItkFilterVisualPanel1.class, "NewItkFilterVisualPanel1.folderTextField.text")); // NOI18N
        folderTextField.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(NewItkFilterVisualPanel1.class, "NewItkFilterVisualPanel1.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(locationLabel, org.openide.util.NbBundle.getMessage(NewItkFilterVisualPanel1.class, "NewItkFilterVisualPanel1.locationLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(locationLabel)
                    .addComponent(jLabel3))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parentClassComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(locationComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(classNameTextField)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(multiThreadedCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(folderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(26, 26, 26))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(classNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationLabel)
                    .addComponent(locationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(folderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parentClassComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(multiThreadedCheckBox)
                .addContainerGap(125, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        // Show the browse dialog
        SourceGroup group = getTargetGroup();
        FileObject fo = BrowseFolders.showDialog(new SourceGroup[]{group},
                project,
                folderTextField.getText().replace(File.separatorChar, '/')); // NOI18N

        if (fo != null && fo.isFolder()) {
            String relPath = FileUtil.getRelativePath(group.getRootFolder(), fo);
            folderTextField.setText(relPath.replace('/', File.separatorChar)); // NOI18N
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void parentClassComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parentClassComboBoxActionPerformed
        changeSupport.fireChange();
    }//GEN-LAST:event_parentClassComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JTextField classNameTextField;
    private javax.swing.JTextField folderTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox locationComboBox;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JCheckBox multiThreadedCheckBox;
    private javax.swing.JComboBox parentClassComboBox;
    // End of variables declaration//GEN-END:variables
}
