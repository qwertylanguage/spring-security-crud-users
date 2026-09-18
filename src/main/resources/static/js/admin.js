async function getUsers() {
    const response = await fetch('/api/admin/users');
    const users = await response.json();

    renderUsers(users);
}

function renderUsers(users) {
    const tableBody = document.getElementById('usersTableBody');

    tableBody.innerHTML = '';

    users.forEach(user => {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.firstName}</td>
            <td>${user.lastName}</td>
            <td>${user.age}</td>
            <td>${user.username}</td>
            <td>${user.roles.join(', ')}</td>
            <td>
                <button class="btn btn-info btn-sm text-white"
                        onclick="openEditModal(${user.id})">
                     Edit
                </button>
            </td>
            <td>
                <button class="btn btn-danger btn-sm"
                        onclick="openDeleteModal(${user.id})">
                    Delete
                </button>
            </td>
        `;

        tableBody.appendChild(row);
    });
}

async function openEditModal(id) {
    const userResponse = await fetch(`/api/admin/users/${id}`);
    const user = await userResponse.json();

    const rolesResponse = await fetch('/api/admin/users/roles');
    const allRoles = await rolesResponse.json();

    document.getElementById('editId').value = user.id;
    document.getElementById('editFirstName').value = user.firstName;
    document.getElementById('editLastName').value = user.lastName;
    document.getElementById('editAge').value = user.age;
    document.getElementById('editUsername').value = user.username;
    document.getElementById('editPassword').value = '';
    document.getElementById('editPasswordError').textContent = '';

    const rolesContainer = document.getElementById('editRoles');

    rolesContainer.innerHTML = '';

    allRoles.forEach(role => {
        const checked = user.roles.includes(role.name);

        rolesContainer.innerHTML += `
            <div class="form-check">
                <input class="form-check-input"
                       type="checkbox"
                       value="${role.id}"
                       id="editRole-${role.id}"
                       ${checked ? 'checked' : ''}>

                <label class="form-check-label"
                       for="editRole-${role.id}">
                    ${role.name}
                </label>
            </div>
        `;
    });

    const modal = new bootstrap.Modal(
        document.getElementById('editModal')
    );

    modal.show();
}

document.getElementById('editForm').addEventListener('submit', async function (event) {
    event.preventDefault();

    document.getElementById('editFirstNameError').textContent = '';
    document.getElementById('editLastNameError').textContent = '';
    document.getElementById('editAgeError').textContent = '';
    document.getElementById('editUsernameError').textContent = '';
    document.getElementById('editPasswordError').textContent = '';
    document.getElementById('editRolesError').textContent = '';

    const id = document.getElementById('editId').value;

    const roleIds = Array.from(
        document.querySelectorAll('#editRoles input:checked')
    ).map(checkbox => Number(checkbox.value));

    const user = {
        firstName: document.getElementById('editFirstName').value,
        lastName: document.getElementById('editLastName').value,
        age: document.getElementById('editAge').value,
        username: document.getElementById('editUsername').value,
        password: document.getElementById('editPassword').value,
        roleIds: roleIds
    };
    //тест
    //жопа с ролями
    console.log('Отправляем пользователя:', user);

    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch(`/api/admin/users/${id}`, {
        method: 'PUT',

        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },

        body: JSON.stringify(user)
    });

    if (response.ok) {

        bootstrap.Modal.getInstance(
            document.getElementById('editModal')
        ).hide();

        await getUsers();

    } else {

        const errors = await response.json();

        if (errors.firstName) {
            document.getElementById('editFirstNameError').textContent =
                errors.firstName;
        }

        if (errors.lastName) {
            document.getElementById('editLastNameError').textContent =
                errors.lastName;
        }

        if (errors.age) {
            document.getElementById('editAgeError').textContent =
                errors.age;
        }

        if (errors.username) {
            document.getElementById('editUsernameError').textContent =
                errors.username;
        }

        if (errors.password) {
            document.getElementById('editPasswordError').textContent =
                errors.password;
        }

        if (errors.roleIds) {
            document.getElementById('editRolesError').textContent =
                errors.roleIds;
        }
    }
});

async function openDeleteModal(id) {
    const response = await fetch(`/api/admin/users/${id}`);
    const user = await response.json();

    const modal = document.getElementById('deleteModal');

    modal.dataset.userId = user.id;

    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();
}

//удаление

document.getElementById('confirmDeleteButton').addEventListener('click', async function () {

    const modal = document.getElementById('deleteModal');
    const id = modal.dataset.userId;

    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch(`/api/admin/users/${id}`, {
        method: 'DELETE',
        headers: {
            [header]: token
        }
    });

    if (response.ok) {
        bootstrap.Modal.getInstance(modal).hide();

        await getUsers();
    }
});

const allUsersTab = document.getElementById('allUsersTab');
const newUserTab = document.getElementById('newUserTab');
const newUserSection = document.getElementById('newUserSection');
const allUsersSection = document.getElementById('allUsersSection');

allUsersTab.addEventListener('click', function () {
    allUsersSection.style.display = 'block';
    newUserSection.style.display = 'none';

    allUsersTab.classList.add('active');
    newUserTab.classList.remove('active');
});

newUserTab.addEventListener('click', function () {
    allUsersSection.style.display = 'none';
    newUserSection.style.display = 'block';

    newUserTab.classList.add('active');
    allUsersTab.classList.remove('active');

    loadNewUserRoles();
});

async function loadNewUserRoles() {
    const response = await fetch('/api/admin/users/roles');
    const roles = await response.json();

    const rolesContainer = document.getElementById('newRoles');
    rolesContainer.innerHTML = '';

    roles.forEach(role => {
        rolesContainer.innerHTML += `
            <div class="form-check">
                <input class="form-check-input"
                       type="checkbox"
                       value="${role.id}"
                       id="newRole-${role.id}">

                <label class="form-check-label"
                       for="newRole-${role.id}">
                    ${role.name}
                </label>
            </div>
        `;
    });
}

document.getElementById('newUserForm').addEventListener('submit', async function (event) {
    event.preventDefault();

    const roleIds = Array.from(
        document.querySelectorAll('#newRoles input:checked')
    ).map(checkbox => Number(checkbox.value));

    const user = {
        firstName: document.getElementById('newFirstName').value,
        lastName: document.getElementById('newLastName').value,
        age: document.getElementById('newAge').value,
        username: document.getElementById('newUsername').value,
        password: document.getElementById('newPassword').value,
        roleIds: roleIds


    };

    document.getElementById('newFirstNameError').textContent = '';
    document.getElementById('newLastNameError').textContent = '';
    document.getElementById('newAgeError').textContent = '';
    document.getElementById('newUsernameError').textContent = '';
    document.getElementById('newPasswordError').textContent = '';
    document.getElementById('newRolesError').textContent = '';

    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch('/api/admin/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify(user)
    });

    if (response.ok) {
        document.getElementById('newUserForm').reset();

        allUsersSection.style.display = 'block';
        newUserSection.style.display = 'none';

        allUsersTab.classList.add('active');
        newUserTab.classList.remove('active');

        await getUsers();
    }
    else {
        const errors = await response.json();

        if (errors.firstName) {
            document.getElementById('newFirstNameError').textContent = errors.firstName;
        }

        if (errors.lastName) {
            document.getElementById('newLastNameError').textContent = errors.lastName;
        }

        if (errors.age) {
            document.getElementById('newAgeError').textContent = errors.age;
        }

        if (errors.username) {
            document.getElementById('newUsernameError').textContent = errors.username;
        }

        if (errors.password) {
            document.getElementById('newPasswordError').textContent = errors.password;
        }

        if (errors.roleIds) {
            document.getElementById('newRolesError').textContent = errors.roleIds;
        }
    }
});

getUsers();