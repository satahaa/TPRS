import re

file_path = '/var/www/html/TPRS/admin-dashboard.html'
with open(file_path, 'r') as f:
    content = f.read()

# Add drag and drop logic to the JS, and modify addListRow
new_logic = """        // ===== Drag and Drop Ordering =====
        let draggedRow = null;

        function attachDragEvents(row) {
            row.draggable = true;
            row.addEventListener('dragstart', (e) => {
                draggedRow = row;
                row.style.opacity = '0.5';
                e.dataTransfer.effectAllowed = 'move';
            });
            row.addEventListener('dragend', () => {
                draggedRow.style.opacity = '1';
                draggedRow = null;
            });
            row.addEventListener('dragover', (e) => {
                e.preventDefault();
                e.dataTransfer.dropEffect = 'move';
                const container = row.parentElement;
                
                // Determine insertion point
                const siblings = [...container.querySelectorAll('.settings-row:not([style*="opacity: 0.5"])')];
                const nextSibling = siblings.find(sibling => {
                    const box = sibling.getBoundingClientRect();
                    return e.clientY <= box.top + box.height / 2;
                });
                
                if (draggedRow && draggedRow !== row) {
                    container.insertBefore(draggedRow, nextSibling || null);
                }
            });
        }

        function createInputsHtml(inputs) {"""

content = content.replace("        function createInputsHtml(inputs) {", new_logic)

old_addListRow = """        function addListRow(containerId, inputs) {
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
        }"""

new_addListRow = """        function addListRow(containerId, inputs) {
            const container = document.getElementById(containerId);
            const row = document.createElement('div');
            row.className = 'settings-row';
            row.style.display = 'flex';
            row.style.gap = '0.5rem';
            row.style.alignItems = 'center';
            row.style.cursor = 'grab';
            row.style.transition = 'all 0.2s';
            
            row.innerHTML = `
                <span class="material-icons" style="color:#666; cursor:grab; font-size:1.2rem;">drag_indicator</span>
            ` + createInputsHtml(inputs) + `
                <button type="button" tabindex="-1" onclick="this.parentElement.remove()" style="background:none; border:none; color:#f44336; cursor:pointer; display:flex; align-items:center; padding:5px;" title="Delete">
                    <span class="material-icons" style="font-size:1.2rem;">delete</span>
                </button>
            `;
            
            // Allow clicking to edit, drag logic handles cursor states
            row.addEventListener('mousedown', () => row.style.cursor = 'grabbing');
            row.addEventListener('mouseup', () => row.style.cursor = 'grab');
            
            attachDragEvents(row);
            container.appendChild(row);
        }"""

content = content.replace(old_addListRow, new_addListRow)

with open(file_path, 'w') as f:
    f.write(content)
print("Drag and drop added successfully")
