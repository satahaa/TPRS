import re

file_path = '/var/www/html/TPRS/admin-dashboard.html'
with open(file_path, 'r') as f:
    text = f.read()

# Replace the System Settings container with `overview-card` classes
new_settings_html = """        <!-- ===== Settings Section ===== -->
        <div class="section" id="section-settings">
            <div class="overview-grid" style="display:flex; flex-direction:column; gap:1.5rem;">
                <div class="overview-card" style="width:100%;">
                    <div style="display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid #eee; padding-bottom:1rem; margin-bottom:1.5rem;">
                        <h3 class="overview-card-title" style="margin:0; border:none; padding:0; display:flex; align-items:center; gap:0.5rem; font-size:1.3rem;">
                            <span class="material-icons" style="color:#2196F3; font-size:1.6rem;">settings</span> System Settings
                        </h3>
                        <button class="primary-btn" onclick="saveSystemSettings()" style="padding:0.6rem 1.2rem; display:flex; align-items:center; gap:0.4rem; font-size:0.95rem;">
                            <span class="material-icons" style="font-size:1.1rem;">save</span> Save Settings
                        </button>
                    </div>
                    <p style="color:#666; font-size:0.95rem; margin-top:0; margin-bottom:1.5rem;">Modify the dynamic configuration values for the system.</p>
                    
                    <div style="display:flex; flex-direction:column; gap:2.5rem;">
                        <!-- Top Row: Sessions and Specs -->
                        <div style="display:flex; gap:2rem; flex-wrap:wrap;">
                            <!-- SESSIONS -->
                            <div style="flex:1; min-width:300px; background:#f9f9f9; padding:1.2rem; border-radius:10px; border:1px solid #e0e0e0;">
                                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem; padding-bottom:0.8rem; border-bottom:1px solid #ddd;">
                                    <label style="font-size:1.05rem; font-weight:700; color:#333; margin:0; display:flex; align-items:center; gap:0.4rem;">
                                        <span class="material-icons" style="color:#f44336; font-size:1.2rem;">date_range</span> Academic Sessions
                                    </label>
                                    <button onclick="addSessionRow()" class="primary-btn" style="padding:0.4rem 0.8rem; display:flex; align-items:center; gap:0.2rem; font-size:0.85rem; border-radius:5px;"><span class="material-icons" style="font-size:1.1rem;">add</span> Add</button>
                                </div>
                                <div id="settings-sessions-list" style="display:flex; flex-direction:column; gap:0.6rem; max-height:280px; overflow-y:auto; padding-right:8px;"></div>
                            </div>

                            <!-- SPECIALIZATIONS -->
                            <div style="flex:1; min-width:300px; background:#f9f9f9; padding:1.2rem; border-radius:10px; border:1px solid #e0e0e0;">
                                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem; padding-bottom:0.8rem; border-bottom:1px solid #ddd;">
                                    <label style="font-size:1.05rem; font-weight:700; color:#333; margin:0; display:flex; align-items:center; gap:0.4rem;">
                                        <span class="material-icons" style="color:#4caf50; font-size:1.2rem;">school</span> Specializations
                                    </label>
                                    <button onclick="addSpecializationRow()" class="primary-btn" style="padding:0.4rem 0.8rem; display:flex; align-items:center; gap:0.2rem; font-size:0.85rem; border-radius:5px;"><span class="material-icons" style="font-size:1.1rem;">add</span> Add</button>
                                </div>
                                <div id="settings-specializations-list" style="display:flex; flex-direction:column; gap:0.6rem; max-height:280px; overflow-y:auto; padding-right:8px;"></div>
                            </div>
                        </div>

                        <!-- Bottom Row: Departments and Degrees -->
                        <div style="display:flex; gap:2rem; flex-wrap:wrap;">
                            <!-- DEPARTMENTS -->
                            <div style="flex:1; min-width:300px; background:#f9f9f9; padding:1.2rem; border-radius:10px; border:1px solid #e0e0e0;">
                                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem; padding-bottom:0.8rem; border-bottom:1px solid #ddd;">
                                    <label style="font-size:1.05rem; font-weight:700; color:#333; margin:0; display:flex; align-items:center; gap:0.4rem;">
                                        <span class="material-icons" style="color:#ff9800; font-size:1.2rem;">domain</span> Departments
                                    </label>
                                    <button onclick="addDepartmentRow()" class="primary-btn" style="padding:0.4rem 0.8rem; display:flex; align-items:center; gap:0.2rem; font-size:0.85rem; border-radius:5px;"><span class="material-icons" style="font-size:1.1rem;">add</span> Add</button>
                                </div>
                                <div style="display:flex; gap:0.5rem; margin-bottom:0.6rem; color:#777; font-size:0.85rem; font-weight:700; padding:0 5px; text-transform:uppercase; letter-spacing:0.5px;">
                                    <div style="flex:1; padding-left:1.8rem;">ID</div>
                                    <div style="flex:3;">Name</div>
                                    <div style="width:30px;"></div>
                                </div>
                                <div id="settings-departments-list" style="display:flex; flex-direction:column; gap:0.6rem; max-height:300px; overflow-y:auto; padding-right:8px;"></div>
                            </div>

                            <!-- DEGREES -->
                            <div style="flex:1; min-width:300px; background:#f9f9f9; padding:1.2rem; border-radius:10px; border:1px solid #e0e0e0;">
                                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem; padding-bottom:0.8rem; border-bottom:1px solid #ddd;">
                                    <label style="font-size:1.05rem; font-weight:700; color:#333; margin:0; display:flex; align-items:center; gap:0.4rem;">
                                        <span class="material-icons" style="color:#9c27b0; font-size:1.2rem;">menu_book</span> Degree Types
                                    </label>
                                    <button onclick="addDegreeRow()" class="primary-btn" style="padding:0.4rem 0.8rem; display:flex; align-items:center; gap:0.2rem; font-size:0.85rem; border-radius:5px;"><span class="material-icons" style="font-size:1.1rem;">add</span> Add</button>
                                </div>
                                <div style="display:flex; gap:0.5rem; margin-bottom:0.6rem; color:#777; font-size:0.85rem; font-weight:700; padding:0 5px; text-transform:uppercase; letter-spacing:0.5px;">
                                    <div style="flex:1; padding-left:1.8rem;">ID</div>
                                    <div style="flex:3;">Name</div>
                                    <div style="width:30px;"></div>
                                </div>
                                <div id="settings-degrees-list" style="display:flex; flex-direction:column; gap:0.6rem; max-height:300px; overflow-y:auto; padding-right:8px;"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>"""

# Remove old System Settings HTML from <div class="section" id="section-settings">... to <!-- ===== Edit Student Modal ===== -->
import re
new_text = re.sub(
    r'(?s)<!-- ===== Settings Section ===== -->.*?<!-- ===== Edit Student Modal ===== -->',
    new_settings_html + '\n\n    <!-- ===== Edit Student Modal ===== -->',
    text
)

# And make the styled inputs look like the normal page
create_html_func = """function createInputsHtml(inputs) {
            return inputs.map(i => `<input type="text" style="flex:${i.flex}; font-size:0.95rem; padding:0.6rem; border:1px solid #ccc; border-radius:6px; outline:none; transition:border-color 0.2s; box-shadow:inset 0 1px 3px rgba(0,0,0,0.05);" class="${i.class}" value="${i.val}" placeholder="${i.placeholder}" onfocus="this.style.borderColor='#2196F3'" onblur="this.style.borderColor='#ccc'">`).join('');
        }"""
new_text = re.sub(
    r'(?s)function createInputsHtml\(inputs\) \{.*?\}',
    create_html_func,
    new_text
)

with open(file_path, 'w') as f:
    f.write(new_text)

print("Updated Settings CSS to match Admin Overview Theme")
