
export const mockGraphQLResponse = (page, operation, response) => {
    return page.route(operation, (route) => {
        route.fulfill({
            json: response,
        });
    });
};

export const mockGraphQLRequest = (operation, data) => {
    return {
        request: {
            query: '',
            variables: {},
        },
        result: {
            data,
        },
    };
};