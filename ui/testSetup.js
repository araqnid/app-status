import {configure} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';

configure({ adapter: new Adapter() });

// don't try and load images in tests - instead return the filename

function returnFileName(module, filepath) {
    var filename = filepath.substr(filepath.lastIndexOf('/') + 1);
    return module._compile('module.exports = "' + filename + '";', filepath)
}

require.extensions['.png'] = returnFileName;
require.extensions['.css'] = returnFileName;
require.extensions['.svg'] = returnFileName;
