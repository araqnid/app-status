import { render, screen } from '@testing-library/react'
import { act } from 'react-dom/test-utils';
import Home from '@/pages/index'
import mock from 'xhr-mock'

beforeEach(() => {
    mock.setup()
})

afterEach(() => {
    mock.teardown()
})

describe('Home', () => {
    it('starts loading on mount', async () => {
        mock.get('/_api/info/version', {
            body: { title: "example", version: "0.0.0", vendor: null }
        })
        mock.get('/_api/info/readiness', {
            body: "READY"
        })
        mock.get('/_api/info/status', {
            body: { status: "OK", components: { example: { label: "Example", priority: "OK", text: "Example is OK" } } }
        })

        render(<Home />)

        await act(async () => {
            await Promise.resolve()
        })
        
        expect(screen.getByText("Application ready")).toBeInTheDocument()
        expect(screen.getByText("example 0.0.0 - OK")).toBeInTheDocument()
        expect(screen.getByText("Example - OK")).toBeInTheDocument()
        expect(screen.getByText("Example is OK")).toBeInTheDocument()
    })
})
