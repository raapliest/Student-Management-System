
// SAPMS — Global JavaScript
document.addEventListener('DOMContentLoaded', function() {

  // ── Modal handling ──
  window.openModal = function(id) {
    const el = document.getElementById(id);
    if (el) { el.classList.add('open'); document.body.style.overflow = 'hidden'; }
  };

  window.closeModal = function(id) {
    if (id) {
      const el = document.getElementById(id);
      if (el) el.classList.remove('open');
    } else {
      document.querySelectorAll('.modal-overlay').forEach(m => m.classList.remove('open'));
    }
    document.body.style.overflow = '';
  };

  // Close on backdrop click
  document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', function(e) {
      if (e.target === overlay) closeModal();
    });
  });

  // Close on Escape
  document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') closeModal();
  });

  // ── Auto-dismiss alerts ──
  document.querySelectorAll('.alert').forEach(alert => {
    setTimeout(() => {
      alert.style.transition = 'opacity .5s ease';
      alert.style.opacity = '0';
      setTimeout(() => alert.remove(), 500);
    }, 4000);
  });

  // ── Active nav item ──
  const path = window.location.pathname;
  document.querySelectorAll('.nav-item').forEach(item => {
    const href = item.getAttribute('href');
    if (href && path.startsWith(href) && href !== '/') {
      item.classList.add('active');
    }
  });

  // ── Animate stat values ──
  document.querySelectorAll('.stat-value[data-value]').forEach(el => {
    const target = parseInt(el.dataset.value) || 0;
    let current = 0;
    const duration = 800;
    const step = target / (duration / 16);
    const timer = setInterval(() => {
      current = Math.min(current + step, target);
      el.textContent = Math.floor(current);
      if (current >= target) clearInterval(timer);
    }, 16);
  });

  // ── Progress bar animation ──
  document.querySelectorAll('.progress-fill[data-width]').forEach(el => {
    const w = el.dataset.width;
    setTimeout(() => { el.style.width = w + '%'; }, 200);
  });

});

// Edit student modal
function openEditStudent(btn) {
  document.getElementById('eStudentNo').value  = btn.dataset.no;
  document.getElementById('eStudentName').value = btn.dataset.name;
  document.getElementById('eStudentEmail').value = btn.dataset.email || '';
  document.getElementById('editStudentForm').action = '/admin/students/edit/' + btn.dataset.no;
  openModal('editStudentModal');
}

// Edit lecturer modal
function openEditLecturer(btn) {
  document.getElementById('eLectName').value  = btn.dataset.name;
  document.getElementById('eLectEmail').value = btn.dataset.email || '';
  document.getElementById('editLecturerForm').action = '/admin/lecturers/edit/' + btn.dataset.id;
  openModal('editLecturerModal');
}

// Edit course modal
function openEditCourse(btn) {
  document.getElementById('eCourseCode').value    = btn.dataset.code;
  document.getElementById('eCourseName').value    = btn.dataset.name;
  document.getElementById('eCourseCredits').value = btn.dataset.credits;
  document.getElementById('eCourseMajor').value   = btn.dataset.major;
  document.getElementById('eCourseLect').value    = btn.dataset.lecturer;
  document.getElementById('editCourseForm').action = '/admin/courses/edit/' + btn.dataset.id;
  openModal('editCourseModal');
}
