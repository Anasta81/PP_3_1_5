$(function () {

    $(document).on('submit', '.js-newForm', function (e) {
        e.preventDefault()
        const $form = $(this)
        const el = this
        const data = new FormData(el)
        data.append('_method', 'POST')

        $.ajax({
            url: `/rest/new`,
            type: 'POST',
            data: data,
            contentType: false,
            processData: false,
            success: (response) => {
                const htmlRow = createRow(response)
                $('.js-table').append(htmlRow)
                $form.find('input[type="text"]').val('')
                $form.find('input[type="password"]').val('')
                $('#nav-users-table').trigger('click')
            },
            error: (err) => {
                console.log('error')
                console.log(err)
            }
        })
    })

    $(document).on('submit', '.js-deleteForm', function (e) {
        e.preventDefault()
        const $form = $(this)
        const id = $form.attr('data-id')
        const data = new FormData()
        data.append('_method', 'DELETE')

        $.ajax({
            url: `/rest/delete/${id}`,
            type: 'POST',
            data: data,
            contentType: false,
            processData: false,
            success: (response) => {
                const $row = $(`.js-row[data-id="${id}"]`)
                setTimeout(() => {
                    $row.remove()
                }, 300)
            },
            error: (err) => {
                console.log('error')
                console.log(err)
            }
        })
    })

    $(document).on('submit', '.js-editForm', function (e) {
        e.preventDefault()
        const $form = $(this)
        const id = $form.attr('data-id')
        const el = this
        const data = new FormData(el)
        data.append('_method', 'PUT')

        $.ajax({
            url: `/rest/update/${id}`,
            type: 'POST',
            data: data,
            contentType: false,
            processData: false,
            success: (response) => {
                const $row = $(`.js-row[data-id="${id}"]`)
                $row.find('.js-row-item')
                    .each(function () {
                        const $el = $(this)
                        const name = $el.attr('data-name')
                        if (name in response) {
                            $el.text(response[name])
                        }
                    })
                $row.find('.js-row-roles').html(createRoles(response.roles))
                $form.find('.js-editForm-password').val('')
            },
            error: (err) => {
                console.log('error')
                console.log(err)
            }
        })
    })

    function renderTable() {
        $.ajax({
            url: `/rest/users`,
            type: 'GET',
            dataType: 'json',
            success: (response) => {
                const html = response.map(el => createRow(el)).join('')
                $('.js-table').html(html)
                console.log(response)
            }
        })
    }

    function createRoles(roles) {
        return roles.map(el => `<span class="mx-1">${el.authority}</span>`).join('')
    }

    function createRow(data) {
        const id = data.id
        return `
            <tr class="js-row" data-id="${id}">
                <td class="js-row-item" data-name="name">${data['name']}</td>
                <td class="js-row-item" data-name="lastName">${data['lastName']}</td>
                <td class="js-row-item" data-name="ability">${data['ability']}</td>
                <td class="js-row-item" data-name="alias">${data['alias']}</td>
                <td class="js-row-item" data-name="username">${data['username']}</td>
                <td class="js-row-roles">
                    ${createRoles(data['roles'])}
                </td>
                <td>
                    ${createForm(data, 'edit')}
                </td>
                <td>
                    ${createForm(data, 'delete')}
                </td>
            </tr>
        `
    }

    function createForm(data, type) {
        const id = data.id
        const formGroupNames = ['name', 'lastName', 'ability', 'alias', 'username']

        const formGroupHtml = formGroupNames.map(el => {
            const labelId = ((type === 'edit') ? 'edit-' : 'delete-') + id + '-' + el
            return `
                <div class="form-group">
                    <label for="${labelId}" class="font-weight-bold text-capitalize">${el}</label>
                    <input type="text" class="form-control" value="${data[el]}" name="${el}" id="${labelId}" ${(type === 'edit') ? '' : 'readonly'}>
                </div>
            `
        }).join('')

        const formPassword = (type === 'edit') ? `
            <div class="form-group">
                <label for="password-${id}" class="font-weight-bold">Password</label>
                <input type="password" class="form-control js-editForm-password" name="password" id="password-${id}">
            </div>
        ` : '';

        return `
            <button type="button" 
                class="btn ${(type === 'edit') ? 'btn-primary' : 'btn-danger'}" 
                data-bs-toggle="modal" 
                data-bs-target="#${(type === 'edit') ? 'edit' : 'delete'}-form-${id}" 
            >
                ${(type === 'edit') ? 'Edit' : 'Delete'}
            </button>
            <div class="modal fade" id="${(type === 'edit') ? 'edit' : 'delete'}-form-${id}" 
                tabindex="-1" 
                role="dialog" 
                aria-hidden="true"
            >
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">${(type === 'edit') ? 'Edit user' : 'Delete user'}</h5>
                            <button type="button" class="close" data-bs-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body col text-center">
                            <form class="${(type === 'edit') ? 'js-editForm' : 'js-deleteForm'}" data-id="${id}">
                                ${formGroupHtml}
                                ${formPassword}
                                <div class="form-group">
                                    <label for="${((type === 'edit') ? 'edit-' : 'delete-') + id + '-role'}">Your role:</label>
                                    <select name="role" id="${((type === 'edit') ? 'edit-' : 'delete-') + id + '-role'}" multiple="multiple" size="2" ${(type === 'edit') ? '' : 'disabled'}>
                                        <option value="ROLE_USER">User</option>
                                        <option value="ROLE_ADMIN">Admin</option>
                                    </select>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                    <input type="submit" class="btn ${(type === 'edit') ? 'btn-primary' : 'btn-danger'}" value="${(type === 'edit') ? 'Edit' : 'Delete'}" data-bs-dismiss="modal">
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        `
    }

    renderTable()
})