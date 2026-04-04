import os
import re

file_path = '/var/www/html/TPRS/admin-dashboard.html'
with open(file_path, 'r') as f:
    content = f.read()

# 1. Remove hardcoded `<option>` from `assignSessionFilter`
# Find everything from <option value="2029-2030"> to <option value="2020-2021">
content = re.sub(
    r'<option value="\d{4}-\d{4}">\d{4}-\d{4}</option>\s*',
    '',
    content
)

# 2. Add 'Settings' button in Tab Bar
tab_bar_insertion = """        <button class="tab-btn" data-tab="assignments" onclick="switchTab('assignments')">
            <span class="material-icons" style="font-size:1.1rem;">assignment</span> Assignments
        </button>
        <button class="tab-btn" data-tab="settings" onclick="switchTab('settings')">
            <span class="material-icons" style="font-size:1.1rem;">settings</span> Settings
        </button>"""

content = content.replace(
    '''        <button class="tab-btn" data-tab="assignments" onclick="switchTab('assignments')">
            <span class="material-icons" style="font-size:1.1rem;">assignment</span> Assignments
        </button>''',
    tab_bar_insertion
)

# 3. Insert the full Settings section HTML
settings_section_html = '''        <!-- ===== Settings Section ===== -->
        <div class="section" id="section-settings">
            <div class="settings-container" style="background:#151520; padding:1.5rem; border-radius:10px; border:1px solid #2e2e42;">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem;">
                    <h3 style="color:#b5b5cc; font-size:1.2rem; display:flex; align-items:center; gap:0.5rem; margin:0;">
                        <span class="material-icons" style="color:#d63d86;">settings</span> System Settings
                    </h3>
                    <button class="primary-btn" onclick="saveSystemSettings()" style="padding:0.6rem 1.2rem; display:flex; align-items:center; gap:0.4rem; font-size:0.9rem;">
                        <span class="material-icons" style="font-size:1.1rem;">save</span> Save Settings
                    </button>
                </div>
                <p style="color:#b5b5cc; font-size:0.9rem; margin-bottom:1.5rem;">Modify the dynamic configuration values for the system.</p>
                
                <div style="display:flex; flex-direction:column; gap:2.5rem;">
                    <div style="display:flex; gap:1.5rem; flex-wrap:wrap;">
                        <!-- SESSIONS -->
                        <div style="flex:1; min-width:250px;">
                            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.8rem; background:#1e1e2e; padding:10px; border-radius:8px;">
                                <label style="font-size:0.95rem; font-weight:600; color:#fff; margin:0;">Academic Sessions</label>
                                <button onclick="addSessionRow()" class="primary-btn" style="padding:0.3rem 0.6rem; display:flex; align-items:center; gap:0.2rem; font-size:0.8rem; border-radius:5px;"><span class="material-icons" style="font-size:1rem;">add</span> Add</button>
                            </div>
                            <div id="settings-sessions-list" style="display:flex; flex-direction:column; gap:0.5rem; max-height:250px; overflow-y:auto; padding-right:5px;"></div>
                        </div>

                        <!-- SPECIALIZATIONS -->
                        <div style="flex:2; min-width:350px;">
                            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.8rem; background:#1e1e2e; padding:10px; border-radius:8px;">
                                <label style="font-size:0.95rem; font-weight:600; color:#fff; margin:0;">Specializations</label>
                                <button onclick="addSpecializationRow()" class="primary-btn" style="padding:0.3rem 0.6rem; display:flex; align-items:center; gap:0.2rem; font-size:0.8rem; border-radius:5px;"><span class="material-icons" style="font-size:1rem;">add</span> Add</button>
                            </div>
                            <div id="settings-specializations-list" style="display:flex; flex-direction:column; gap:0.5rem; max-height:250px; overflow-y:auto; padding-right:5px;"></div>
                        </div>
                    </div>

                    <div style="display:flex; gap:1.5rem; flex-wrap:wrap;">
                        <!-- DEPARTMENTS -->
                        <div style="flex:1; min-width:350px;">
                            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.8rem; background:#1e1e2e; padding:10px; border-radius:8px;">
                                <label style="font-size:0.95rem; font-weight:600; color:#fff; margin:0;">Departments</label>
                                <button onclick="addDepartmentRow()" class="primary-btn" style="padding:0.3rem 0.6rem; display:flex; align-items:center; gap:0.2rem; font-size:0.8rem; border-radius:5px;"><span class="material-icons" style="font-size:1rem;">add</span> Add</button>
                            </div>
                            <div style="display:flex; gap:0.5rem; margin-bottom:0.5rem; color:#888; font-size:0.85rem; font-weight:600; padding:0 5px;">
                                <div style="flex:1;">ID (e.g. CSE)</div>
                                <div style="flex:3;">Name</div>
                                <div style="width:30px;"></div>
                                <div style="width:30px;"></div>
                            </div>
                            <div id="settings-departments-list" style="display:flex; flex-direction:column; gap:0.5rem; max-height:300px; overflow-y:auto; padding-right:5px;"></div>
                        </div>

                        <!-- DEGREES -->
                        <div style="flex:1; min-width:350px;">
                            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.8rem; background:#1e1e2e; padding:10px; border-radius:8px;">
                                <label style="font-size:0.95rem; font-weight:600; color:#fff; margin:0;">Degree Types</label>
                                <button onclick="addDegreeRow()" class="primary-btn" style="padding:0.3rem 0.6rem; display:flex; align-items:center; gap:0.2rem; font-size:0.8rem; border-radius:5px;"><span class="material-icons" style="font-size:1rem;">add</span> Add</button>
                            </div>
                            <div style="display:flex; gap:0.5rem; margin-bottom:0.5rem; color:#888; font-size:0.85rem; font-weight:600; padding:0 5px;">
                                <div style="flex:1;">ID (e.g. bsc)</div>
                                <div style="flex:3;">Name</div>
                                <div style="width:30px;"></div>
                                <div style="width:30px;"></div>
                            </div>
                            <div id="settings-degrees-list" style="display:flex; flex-direction:column; gap:0.5rem; max-height:300px; overflow-y:auto; padding-right:5px;"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- ===== Edit Student Modal ===== -->'''

content = content.replace(
    '''            <div id="assignmentsTable"></div>
        </div>
    </div>

    <!-- ===== Edit Student Modal ===== -->''',
    '''            <div id="assignmentsTable"></div>
        </div>''' + '\n' + settings_section_html
)

# 4. Update JS switchTab call
content = content.replace(
    "else if (tab === 'assignments') loadAllAssignments();",
    "else if (tab === 'assignments') loadAllAssignments();\n            else if (tab === 'settings') loadSystemSettings();"
)

# 5. Insert JS Logic logic before `// Clear all search inputs`
script_logic = """        // ===== Settings Panel =====
        function createInputsHtml(inputs) {
            return inputs.map(i => `<input type="text" class="styled-input ${i.class}" value="${i.val}" style="flex:${i.flex}; font-size:0.9rem;" placeholder="${i.placeholder}">`).join('');
        }
        
        function moveRowUp(btn) {
            const row = btn.parentElement.parentElement;
            if (row.previousElementSibling) {
                row.parentElement.insertBefore(row, row.previousElementSibling);
            }
        }

        function moveRowDown(btn) {
            const row = btn.parentElement.parentElement;
            if (row.nextElementSibling) {
                row.parentElement.insertBefore(row.nextElementSibling, row);
            }
        }

        function addListRow(containerId, inputs) {
            const container = document.getElementById(containerId);
            const row = document.createElement('div');
            row.className = 'settings-row';
            row.style.display = 'flex';
            row.style.gap = '0.5rem';
            row.style.alignItems = 'center';
            
            row.innerHTML = createInputsHtml(inputs) + `
                <div style="display:flex; flex-direction:column; justify-content:center; gap:2px;">
                    <button type="button" tabindex="-1" onclick="moveRowUp(this)" style="background:none; border:none; color:#b5b5cc; cursor:pointer; padding:0; height:15px; display:flex; align-items:center;" title="Move Up">
                        <span class="material-icons" style="font-size:1.3rem;">arrow_drop_up</span>
                    </button>
                    <button type="button" tabindex="-1" onclick="moveRowDown(this)" style="background:none; border:none; color:#b5b5cc; cursor:pointer; padding:0; height:15px; display:flex; align-items:center;" title="Move Down">
                        <span class="material-icons" style="font-size:1.3rem;">arrow_drop_down</span>
                    </button>
                </div>
                <button type="button" tabindex="-1" onclick="this.parentElement.remove()" style="background:none; border:none; color:#f44336; cursor:pointer; display:flex; align-items:center; padding:5px;" title="Delete">
                    <span class="material-icons" style="font-size:1.2rem;">delete</span>
                </button>
            `;
            container.appendChild(row);
        }

        function addSessionRow(val = '') {
            addListRow('settings-sessions-list', [{val, class: 'sess-input', flex: 1, placeholder: '2020-21'}]);
        }
        
        function addSpecializationRow(val = '') {
            addListRow('settings-specializations-list', [{val, class: 'spec-input', flex: 1, placeholder: 'e.g. AI'}]);
        }

        function addDepartmentRow(id = '', name = '') {
            addListRow('settings-departments-list', [
                {val: id, class: 'dep-id', flex: 1, placeholder: 'ID'},
                {val: name, class: 'dep-name', flex: 3, placeholder: 'Name'}
            ]);
        }
        
        function addDegreeRow(id = '', name = '') {
            addListRow('settings-degrees-list', [
                {val: id, class: 'deg-id', flex: 1, placeholder: 'ID'},
                {val: name, class: 'deg-name', flex: 3, placeholder: 'Name'}
            ]);
        }

        async function loadSystemSettings() {
            try {
                document.getElementById('settings-sessions-list').innerHTML = '';
                document.getElementById('settings-specializations-list').innerHTML = '';
                document.getElementById('settings-departments-list').innerHTML = '';
                document.getElementById('settings-degrees-list').innerHTML = '';
                
                const settings = await TPRSApi.getSettings();
                
                if (settings.sessions) settings.sessions.forEach(s => addSessionRow(s));
                if (settings.specializations) settings.specializations.forEach(s => addSpecializationRow(s));
                if (settings.departments) settings.departments.forEach(d => addDepartmentRow(d.id, d.name));
                if (settings.degreeTypes) settings.degreeTypes.forEach(d => addDegreeRow(d.id, d.name));
                
            } catch(e) {
                console.error("Failed to load settings array:", e);
                showToast('Failed to load settings', 'error');
            }
        }

        async function saveSystemSettings() {
            try {
                // Collect Arrays
                const sessions = Array.from(document.querySelectorAll('.sess-input')).map(el => el.value.trim()).filter(v => v);
                const specializations = Array.from(document.querySelectorAll('.spec-input')).map(el => el.value.trim()).filter(v => v);
                
                // Collect Departments
                const departmentRows = Array.from(document.getElementById('settings-departments-list').children);
                const departments = departmentRows.map(row => {
                    return {
                        id: row.querySelector('.dep-id').value.trim(),
                        name: row.querySelector('.dep-name').value.trim()
                    };
                }).filter(d => d.id && d.name);

                // Collect Degrees
                const degreeRows = Array.from(document.getElementById('settings-degrees-list').children);
                const degreeTypes = degreeRows.map(row => {
                    return {
                        id: row.querySelector('.deg-id').value.trim(),
                        name: row.querySelector('.deg-name').value.trim()
                    };
                }).filter(d => d.id && d.name);
                
                const settingsData = { sessions, specializations, departments, degreeTypes };
                
                const res = await TPRSApi.updateSettings(settingsData);
                if(res.success) {
                    showToast('Settings saved successfully', 'success');
                    setTimeout(() => window.location.reload(), 1000);
                } else {
                    showToast(res.message || 'Failed to save settings', 'error');
                }
            } catch(e) {
                console.error(e);
                showToast('Save failed layout mismatch', 'error');
            }
        }

        async function loadAdminSettings() {
            try {
                const settings = await TPRSApi.getSettings();
                
                const sessionFilter = document.getElementById('assignSessionFilter');
                if (sessionFilter && settings.sessions) {
                    sessionFilter.innerHTML = '<option value="">All Sessions</option>';
                    settings.sessions.forEach(session => {
                        const opt = document.createElement('option');
                        opt.value = session;
                        opt.textContent = session;
                        sessionFilter.appendChild(opt);
                    });
                }
            } catch (err) {
                console.error("Failed to load admin settings dropdown:", err);
            }
        }
"""

content = content.replace(
    '        // Clear all search inputs to prevent browser autofill',
    script_logic + '\n        // Clear all search inputs to prevent browser autofill'
)

# 6. Ensure loadAdminSettings and loadOverview are invoked correctly
content = content.replace('        loadOverview();\n\n        // Clear all search inputs', '        loadAdminSettings();\n        loadOverview();\n\n        // Clear all search inputs')

with open(file_path, 'w') as f:
    f.write(content)
print("Updated successfully")
